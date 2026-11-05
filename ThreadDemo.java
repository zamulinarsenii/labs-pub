package labs;

import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ThreadDemo {
  public static class PricesThread extends Thread {
    private final Transport t;

    public PricesThread(Transport t) {
      this.t = t;
    }

    public void run() {
      TransportUtils.printPrices(t);
    }
  }

  public static class ModelsThread extends Thread {
    private final Transport t;

    public ModelsThread(Transport t) {
      this.t = t;
    }

    public void run() {
      TransportUtils.printModels(t);
    }
  }

  public static class TransportSynchronizer {
    private final String[] names;
    private final double[] prices;
    private int current;
    private boolean priceTurn, cancelled;
    private final Object lock = new Object();

    public TransportSynchronizer(Transport t) {
      names = t.getModels();
      prices = t.getPrices();
    }

    public void cancel() {
      synchronized (lock) {
        cancelled = true;
        lock.notifyAll();
      }
    }

    public boolean printModel() throws InterruptedException {
      synchronized (lock) {
        while (priceTurn && !cancelled) lock.wait();
        if (cancelled || current == names.length) return false;
        System.out.println("Model: " + names[current]);
        priceTurn = true;
        lock.notifyAll();
        return true;
      }
    }

    public boolean printPrice() throws InterruptedException {
      synchronized (lock) {
        while (!priceTurn && current < prices.length && !cancelled) lock.wait();
        if (cancelled || current == prices.length) return false;
        System.out.println("Price: " + prices[current++]);
        priceTurn = false;
        lock.notifyAll();
        return true;
      }
    }
  }

  public static class AlternatingModels implements Runnable {
    private final TransportSynchronizer s;

    public AlternatingModels(TransportSynchronizer s) {
      this.s = s;
    }

    public void run() {
      try {
        while (s.printModel()) {}
      } catch (InterruptedException e) {
        s.cancel();
        Thread.currentThread().interrupt();
      }
    }
  }

  public static class AlternatingPrices implements Runnable {
    private final TransportSynchronizer s;

    public AlternatingPrices(TransportSynchronizer s) {
      this.s = s;
    }

    public void run() {
      try {
        while (s.printPrice()) {}
      } catch (InterruptedException e) {
        s.cancel();
        Thread.currentThread().interrupt();
      }
    }
  }

  public static class LockedModels implements Runnable {
    private final Transport t;
    private final Lock l;

    public LockedModels(Transport t, Lock l) {
      this.t = t;
      this.l = l;
    }

    public void run() {
      l.lock();
      try {
        TransportUtils.printModels(t);
      } finally {
        l.unlock();
      }
    }
  }

  public static class LockedPrices implements Runnable {
    private final Transport t;
    private final Lock l;

    public LockedPrices(Transport t, Lock l) {
      this.t = t;
      this.l = l;
    }

    public void run() {
      l.lock();
      try {
        TransportUtils.printPrices(t);
      } finally {
        l.unlock();
      }
    }
  }

  public static class BrandTask implements Runnable {
    private final Transport t;

    public BrandTask(Transport t) {
      this.t = t;
    }

    public void run() {
      System.out.println(Thread.currentThread().getName() + ": " + t.getBrand());
    }
  }

  public static class FileTask implements Runnable {
    private final Path file;
    private final BlockingQueue<Transport> q;

    public FileTask(Path file, BlockingQueue<Transport> q) {
      this.file = file;
      this.q = q;
    }

    public void run() {
      try {
        String brand = Files.readAllLines(file).get(0);
        q.put(new Car(brand, 0));
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new RuntimeException(e);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static void runPair(Thread a, Thread b) throws InterruptedException {
    a.start();
    b.start();
    a.join();
    b.join();
  }

  public static void main(String[] args) throws Exception {
    Transport t = TransportUtils.sample();
    System.out.println("1 Unordered");
    Thread a = new ModelsThread(t), b = new PricesThread(t);
    a.setPriority(Thread.MIN_PRIORITY);
    b.setPriority(Thread.MAX_PRIORITY);
    runPair(a, b);
    System.out.println("2 Alternating");
    TransportSynchronizer s = new TransportSynchronizer(t);
    runPair(new Thread(new AlternatingModels(s)), new Thread(new AlternatingPrices(s)));
    System.out.println("3 Whole groups");
    Lock lock = new ReentrantLock();
    runPair(new Thread(new LockedModels(t, lock)), new Thread(new LockedPrices(t, lock)));
    System.out.println("4 Pool of two");
    ExecutorService pool = Executors.newFixedThreadPool(2);
    for (String brand : new String[] {"Toyota", "Honda", "BMW", "Suzuki"})
      pool.submit(new BrandTask(new Car(brand, 0)));
    pool.shutdown();
    if (!pool.awaitTermination(10, TimeUnit.SECONDS)) pool.shutdownNow();
    System.out.println("5 File producers");
    int capacity = args.length > 1 ? Integer.parseInt(args[1]) : 1;
    Path dir = Path.of(args.length > 0 ? args[0] : "lab03/data");
    BlockingQueue<Transport> q = new ArrayBlockingQueue<>(capacity);
    // Validate inputs before starting consumers so a missing file cannot cause an infinite take().
    for (int i = 1; i <= 5; i++)
      Validation.name(Files.readAllLines(dir.resolve("brand" + i + ".txt")).get(0));
    ExecutorService producers = Executors.newFixedThreadPool(5);
    List<Future<?>> tasks = new ArrayList<>();
    try {
      for (int i = 1; i <= 5; i++)
        tasks.add(producers.submit(new FileTask(dir.resolve("brand" + i + ".txt"), q)));
      ExecutorService consumer = Executors.newSingleThreadExecutor();
      try {
        for (int i = 0; i < 5; i++) {
          Future<Transport> next = consumer.submit(() -> q.take());
          try {
            System.out.println(next.get(10, TimeUnit.SECONDS).getBrand());
          } catch (TimeoutException e) {
            next.cancel(true);
            for (Future<?> f : tasks) if (f.isDone()) f.get();
            throw e;
          }
        }
      } finally {
        consumer.shutdownNow();
      }
      for (Future<?> f : tasks) f.get();
    } finally {
      producers.shutdownNow();
    }
    System.out.println("6 add vs put (deterministic overflow)");
    for (int n = 1; n <= 5; n++) {
      BlockingQueue<Transport> test = new ArrayBlockingQueue<>(n);
      int rejected = 0;
      for (int i = 0; i < 5; i++)
        try {
          test.add(t);
        } catch (IllegalStateException e) {
          rejected++;
        }
      System.out.println("capacity=" + n + ", rejected=" + rejected);
    }
  }
}
