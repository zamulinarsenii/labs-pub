package labs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.*;
import org.junit.jupiter.api.*;

class SocketTest {
  @Test
  void requestResponse() throws Exception {
    try (ServerSocket server = new ServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
      Thread t =
          new Thread(
              () -> {
                try {
                  Protocol.serve(server.accept());
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              });
      t.start();
      try (Socket socket = new Socket(InetAddress.getLoopbackAddress(), server.getLocalPort())) {
        socket.setSoTimeout(5000);
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(new Transport[] {TransportUtils.sample(), new Motorcycle("M", 0)});
        out.flush();
        DataInputStream in = new DataInputStream(socket.getInputStream());
        assertTrue(in.readBoolean());
        assertEquals(150, in.readDouble());
      }
      t.join(5000);
      assertFalse(t.isAlive());
    }
  }

  @Test
  void weightedMean() throws Exception {
    Transport t = new Motorcycle("M", 0);
    t.addModel("X", 600);
    assertEquals(300, Protocol.average(new Transport[] {TransportUtils.sample(), t}));
    assertThrows(IllegalArgumentException.class, () -> Protocol.average(new Transport[0]));
  }
}
