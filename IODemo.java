package labs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class IODemo {
  public static boolean same(Transport a, Transport b) {
    return a.getBrand().equals(b.getBrand())
        && Arrays.equals(a.getModels(), b.getModels())
        && Arrays.equals(a.getPrices(), b.getPrices());
  }

  public static void main(String[] args) throws Exception {
    if (args.length > 0 && args[0].equals("--stdin")) {
      System.err.println("Enter brand, count, then alternating name and price lines:");
      Transport t =
          TransportIO.readTransport(new InputStreamReader(System.in, StandardCharsets.UTF_8));
      TransportIO.writeTransport(t, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
      return;
    }
    Transport t = new Motorcycle("Honda", 0);
    t.addModel("Road", 100);
    t.addModel("Sport", 300);
    try (OutputStream o = new FileOutputStream("transport.bin")) {
      TransportIO.outputTransport(t, o);
    }
    try (InputStream i = new FileInputStream("transport.bin")) {
      System.out.println("Binary equal: " + same(t, TransportIO.inputTransport(i)));
    }
    try (Writer w = new FileWriter("transport.txt", StandardCharsets.UTF_8)) {
      TransportIO.writeTransport(t, w);
    }
    try (Reader r = new FileReader("transport.txt", StandardCharsets.UTF_8)) {
      System.out.println("Text equal: " + same(t, TransportIO.readTransport(r)));
    }
    try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("transport.ser"))) {
      o.writeObject(t);
    }
    try (ObjectInputStream i = new ObjectInputStream(new FileInputStream("transport.ser"))) {
      Transport copy = (Transport) i.readObject();
      System.out.println("Serialized equal: " + same(t, copy));
      System.out.println("Type: " + copy.getClass().getSimpleName());
    }
    TransportIO.writeTransport(t, new OutputStreamWriter(System.out, StandardCharsets.UTF_8));
  }
}
