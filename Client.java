package labs;

import java.io.*;
import java.net.*;

public class Client {
  public static void main(String[] args) throws Exception {
    String host = args.length > 0 ? args[0] : "localhost";
    int port = args.length > 1 ? Integer.parseInt(args[1]) : 5050;
    Transport bike = new Motorcycle("Honda", 0);
    bike.addModel("CB", 600);
    try (Socket s = new Socket()) {
      s.connect(new InetSocketAddress(host, port), 5000);
      s.setSoTimeout(10000);
      ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
      o.writeObject(new Transport[] {TransportUtils.sample(), bike});
      o.flush();
      DataInputStream in = new DataInputStream(s.getInputStream());
      if (in.readBoolean()) System.out.println("Average = " + in.readDouble());
      else throw new IOException(in.readUTF());
    }
  }
}
