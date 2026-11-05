package labs;

import java.io.*;

public final class TransportIO {
  private TransportIO() {}

  public static void outputTransport(Transport v, OutputStream o) throws IOException {
    TransportUtils.outputTransport(v, o);
  }

  public static Transport inputTransport(InputStream i) throws IOException {
    return TransportUtils.inputTransport(i);
  }

  public static void writeTransport(Transport v, Writer w) throws IOException {
    TransportUtils.writeTransport(v, w);
  }

  public static Transport readTransport(Reader r) throws IOException {
    return TransportUtils.readTransport(r);
  }
}
