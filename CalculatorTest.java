package labs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

class CalculatorTest {
  @Test
  void chain() {
    CalculatorEngine e = new CalculatorEngine();
    assertEquals(3, e.operator(3, "+", false));
    assertEquals(8, e.operator(5, "*", false));
    assertEquals(16, e.operator(2, "-", false));
    assertEquals(12, e.equals(4));
    e.clear();
    assertEquals(2, e.operator(2, "+", false));
    assertEquals(2, e.operator(2, "*", true));
    assertEquals(6, e.equals(3));
  }

  @Test
  void errorsAndOperations() {
    CalculatorEngine e = new CalculatorEngine();
    assertThrows(IllegalArgumentException.class, () -> e.sqrt(-1));
    assertThrows(IllegalArgumentException.class, () -> CalculatorEngine.compute(1, 0, "/"));
    assertThrows(IllegalArgumentException.class, () -> CalculatorEngine.parse("abc"));
    assertEquals(2.56, CalculatorEngine.parse("2,56"));
    assertEquals("2,56", CalculatorEngine.format(2.56));
    assertEquals(8, CalculatorEngine.compute(2, 3, "^"));
    assertEquals(3, e.sqrt(9));
  }
}
