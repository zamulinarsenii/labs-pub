package labs;

import java.io.*;
import java.net.*;

public final class Protocol {
  private Protocol() {}

  public static double average(Transport[] vehicles) {
    long count = 0;
    double mean = 0;
    for (Transport t : vehicles) {
      if (t == null) throw new IllegalArgumentException("Null transport");
      for (double p : t.getPrices()) {
        Validation.price(p);
        count++;
        mean += (p - mean) / count;
      }
    }
    if (count == 0) throw new IllegalArgumentException("No models");
    return mean;
  }

  public static void serve(Socket socket) {
    try (socket) {
      socket.setSoTimeout(10000);
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      in.setObjectInputFilter(
          ObjectInputFilter.Config.createFilter(
              "maxdepth=30;maxrefs=100000;maxarray=10000;labs.*;java.lang.*;!*"));
      Object value = in.readObject();
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      try {
        if (!(value instanceof Transport[]))
          throw new IllegalArgumentException("Expected Transport[]");
        double mean = average((Transport[]) value);
        out.writeBoolean(true);
        out.writeDouble(mean);
      } catch (IllegalArgumentException e) {
        out.writeBoolean(false);
        out.writeUTF(e.getMessage());
      }
      out.flush();
    } catch (Exception e) {
      System.err.println("Client error: " + e.getMessage());
    }
  }
}
