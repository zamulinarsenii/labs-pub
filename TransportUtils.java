package labs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class TransportUtils {
  private TransportUtils() {}

  public static double average(Transport t) {
    return Arrays.stream(t.getPrices())
        .average()
        .orElseThrow(() -> new IllegalArgumentException("No models"));
  }

  public static void printModels(Transport t) {
    for (String s : t.getModels()) System.out.println(s);
  }

  public static void printPrices(Transport t) {
    for (double p : t.getPrices()) System.out.println(p);
  }

  public static Transport sample() throws DuplicateModelNameException {
    Transport t = new Car("Toyota", 0);
    t.addModel("Corolla", 100);
    t.addModel("Camry", 200);
    return t;
  }

  public static void main(String[] args) throws Exception {
    for (Transport t : new Transport[] {new Car("Toyota", 0), new Motorcycle("Honda", 0)}) {
      t.addModel("A", 100);
      t.addModel("B", 300);
      t.renameModel("A", "Base");
      t.setPrice("Base", 200);
      printModels(t);
      printPrices(t);
      System.out.println("Average = " + average(t));
      t.removeModel("B");
      try {
        t.addModel("Base", 1);
      } catch (DuplicateModelNameException e) {
        System.out.println("Duplicate detected");
      }
      try {
        t.getPrice("missing");
      } catch (NoSuchModelNameException e) {
        System.out.println("Missing detected");
      }
      try {
        t.setPrice("Base", -1);
      } catch (ModelPriceOutOfBoundsException e) {
        System.out.println("Invalid price detected");
      }
    }
  }

  private static void string(DataOutputStream o, String s) throws IOException {
    byte[] b = s.getBytes(StandardCharsets.UTF_8);
    o.writeInt(b.length);
    o.write(b);
  }

  private static String string(DataInputStream i) throws IOException {
    int n = i.readInt();
    if (n < 0 || n > 1048576) throw new IOException("Invalid string length");
    byte[] b = new byte[n];
    i.readFully(b);
    return new String(b, StandardCharsets.UTF_8);
  }

  private static int count(int n) throws IOException {
    if (n < 0 || n > 100000) throw new IOException("Invalid count");
    return n;
  }

  public static void outputTransport(Transport v, OutputStream out) throws IOException {
    DataOutputStream d = new DataOutputStream(out);
    string(d, v.getBrand());
    d.writeInt(v.getSize());
    String[] s = v.getModels();
    double[] p = v.getPrices();
    for (int i = 0; i < s.length; i++) {
      string(d, s[i]);
      d.writeDouble(p[i]);
    }
    d.flush();
  }

  public static Transport inputTransport(InputStream in) throws IOException {
    DataInputStream d = new DataInputStream(in);
    try {
      Transport v = new Car(string(d), 0);
      int n = count(d.readInt());
      for (int i = 0; i < n; i++) v.addModel(string(d), d.readDouble());
      return v;
    } catch (DuplicateModelNameException | IllegalArgumentException e) {
      throw new IOException("Invalid transport", e);
    }
  }

  public static void writeTransport(Transport v, Writer out) throws IOException {
    PrintWriter p = new PrintWriter(out);
    p.println(v.getBrand());
    p.println(v.getSize());
    String[] n = v.getModels();
    double[] prices = v.getPrices();
    for (int i = 0; i < n.length; i++) {
      p.println(n[i]);
      p.println(prices[i]);
    }
    p.flush();
    if (p.checkError()) throw new IOException("Write failed");
  }

  private static String line(BufferedReader r) throws IOException {
    String s = r.readLine();
    if (s == null) throw new EOFException();
    return s;
  }

  public static Transport readTransport(Reader in) throws IOException {
    BufferedReader r = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
    try {
      Transport v = new Car(line(r), 0);
      int n = count(Integer.parseInt(line(r)));
      for (int i = 0; i < n; i++) v.addModel(line(r), Double.parseDouble(line(r)));
      return v;
    } catch (DuplicateModelNameException | IllegalArgumentException e) {
      throw new IOException("Invalid transport", e);
    }
  }
}
