package labs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import org.junit.jupiter.api.*;

class IOTest {
  @Test
  void roundTrips() throws Exception {
    for (Transport original :
        new Transport[] {new Car("Марка с пробелами", 0), new Motorcycle("Мото", 0)}) {
      original.addModel("Модель с пробелами", 123.45);
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      TransportIO.outputTransport(original, bytes);
      assertTrue(
          IODemo.same(
              original, TransportIO.inputTransport(new ByteArrayInputStream(bytes.toByteArray()))));
      StringWriter text = new StringWriter();
      TransportIO.writeTransport(original, text);
      assertTrue(
          IODemo.same(original, TransportIO.readTransport(new StringReader(text.toString()))));
      ByteArrayOutputStream serialized = new ByteArrayOutputStream();
      try (ObjectOutputStream out = new ObjectOutputStream(serialized)) {
        out.writeObject(original);
      }
      try (ObjectInputStream in =
          new ObjectInputStream(new ByteArrayInputStream(serialized.toByteArray()))) {
        Transport copy = (Transport) in.readObject();
        assertEquals(original.getClass(), copy.getClass());
        assertTrue(IODemo.same(original, copy));
        copy.addModel("new", 4);
        assertEquals(1, original.getSize());
        if (copy instanceof Motorcycle) assertTrue(((Motorcycle) copy).getLastModified() > 0);
      }
    }
  }

  @Test
  void malformed() {
    assertThrows(
        IOException.class,
        () -> TransportIO.inputTransport(new ByteArrayInputStream(new byte[] {0, 0})));
    assertThrows(
        IOException.class, () -> TransportIO.readTransport(new StringReader("Brand\n1\nX\n-1\n")));
  }
}
