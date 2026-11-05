package labs;

public class CalculatorEngine {
  private Double accumulator;
  private String pending;

  public void clear() {
    accumulator = null;
    pending = null;
  }

  public static double parse(String text) {
    try {
      double n = Double.parseDouble(text.replace(',', '.'));
      if (!Double.isFinite(n)) throw new NumberFormatException();
      return n;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Введите число");
    }
  }

  public static String format(double n) {
    if (!Double.isFinite(n)) throw new IllegalArgumentException("Результат вне диапазона");
    return java.math.BigDecimal.valueOf(n).stripTrailingZeros().toPlainString().replace('.', ',');
  }

  public static double compute(double a, double b, String op) {
    double n =
        switch (op) {
          case "+" -> a + b;
          case "-" -> a - b;
          case "*" -> a * b;
          case "/" -> {
            if (b == 0) throw new IllegalArgumentException("Деление на ноль");
            yield a / b;
          }
          case "^" -> Math.pow(a, b);
          default -> throw new IllegalArgumentException("Неизвестная операция");
        };
    if (!Double.isFinite(n)) throw new IllegalArgumentException("Результат вне диапазона");
    return n;
  }

  public double operator(double value, String op, boolean fresh) {
    if (accumulator == null) accumulator = value;
    else if (pending != null && !fresh) accumulator = compute(accumulator, value, pending);
    else if (pending == null) accumulator = value;
    pending = op;
    return accumulator;
  }

  public double equals(double value) {
    if (pending != null) {
      value = compute(accumulator, value, pending);
      pending = null;
    }
    accumulator = value;
    return value;
  }

  public double sqrt(double value) {
    if (value < 0) throw new IllegalArgumentException("Корень из отрицательного числа");
    return Math.sqrt(value);
  }
}
