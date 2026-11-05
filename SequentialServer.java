package labs;

import java.net.*;

public class SequentialServer {
  public static void main(String[] args) throws Exception {
    int port = args.length > 0 ? Integer.parseInt(args[0]) : 5050;
    try (ServerSocket s = new ServerSocket(port, 50, InetAddress.getLoopbackAddress())) {
      System.out.println("Sequential server on " + port);
      while (!Thread.currentThread().isInterrupted()) Protocol.serve(s.accept());
    }
  }
}
