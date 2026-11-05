package labs;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.time.Duration;
import org.junit.jupiter.api.*;

class ThreadsTest {
  @Test
  void alternatingAndEmpty() throws Exception {
    assertTimeoutPreemptively(
        Duration.ofSeconds(10),
        () -> {
          for (int n : new int[] {0, 1, 30}) {
            Transport t = new Car("A", n);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            PrintStream old = System.out;
            try {
              System.setOut(new PrintStream(bytes));
              ThreadDemo.TransportSynchronizer s = new ThreadDemo.TransportSynchronizer(t);
              ThreadDemo.runPair(
                  new Thread(new ThreadDemo.AlternatingPrices(s)),
                  new Thread(new ThreadDemo.AlternatingModels(s)));
            } finally {
              System.setOut(old);
            }
            String out = bytes.toString();
            String[] lines = out.isEmpty() ? new String[0] : out.strip().split("\\R");
            assertEquals(n * 2, lines.length);
            for (int i = 0; i < n; i++) {
              assertEquals("Model: Model" + (i + 1), lines[2 * i]);
              assertEquals("Price: 0.0", lines[2 * i + 1]);
            }
          }
        });
  }
}
