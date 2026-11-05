package labs;

public final class Validation {
  private Validation() {}

  public static String name(String s) {
    if (s == null || s.isBlank() || s.contains("\n") || s.contains("\r"))
      throw new IllegalArgumentException("Name must be nonblank and single-line");
    return s;
  }

  public static void price(double p) {
    if (!Double.isFinite(p) || p < 0)
      throw new ModelPriceOutOfBoundsException("Invalid price: " + p);
  }

  public static void size(int n) {
    if (n < 0) throw new IllegalArgumentException("Negative size");
  }
}
