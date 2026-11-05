package labs;

import java.net.*;
import java.util.concurrent.*;

public class ParallelServer {
  public static void main(String[] args) throws Exception {
    int port = args.length > 0 ? Integer.parseInt(args[0]) : 5050;
    ThreadPoolExecutor pool =
        new ThreadPoolExecutor(4, 4, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(64));
    try (ServerSocket s = new ServerSocket(port, 50, InetAddress.getLoopbackAddress())) {
      System.out.println("Parallel server on " + port);
      while (!Thread.currentThread().isInterrupted()) {
        Socket client = s.accept();
        try {
          pool.execute(() -> Protocol.serve(client));
        } catch (RejectedExecutionException e) {
          client.close();
        }
      }
    } finally {
      pool.shutdownNow();
    }
  }
}
