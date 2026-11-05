package labs;

import java.io.*;

public class Motorcycle implements Transport {
  private static final long serialVersionUID = 1L;

  private class Model implements Serializable {
    private static final long serialVersionUID = 1L;
    String name = null;
    double price = Double.NaN;
    Model prev = null, next = null;
  }

  private String brand;
  private int size = 0;
  private Model head = new Model();
  private transient long lastModified;

  {
    lastModified = System.currentTimeMillis();
    head.next = head;
    head.prev = head;
  }

  public Motorcycle(String brand, int n) {
    this.brand = Validation.name(brand);
    Validation.size(n);
    for (int i = 0; i < n; i++) append("Model" + (i + 1), 0);
  }

  private void touch() {
    lastModified = System.currentTimeMillis();
  }

  public long getLastModified() {
    return lastModified;
  }

  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    touch();
  }

  private void append(String name, double price) {
    Model m = new Model();
    m.name = name;
    m.price = price;
    m.prev = head.prev;
    m.next = head;
    head.prev.next = m;
    head.prev = m;
    size++;
  }

  private Model find(String n) {
    for (Model m = head.next; m != head; m = m.next) if (m.name.equals(n)) return m;
    return null;
  }

  private Model require(String n) throws NoSuchModelNameException {
    Model m = find(n);
    if (m == null) throw new NoSuchModelNameException(n);
    return m;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String b) {
    brand = Validation.name(b);
    touch();
  }

  public int getSize() {
    return size;
  }

  public String[] getModels() {
    String[] a = new String[size];
    int i = 0;
    for (Model m = head.next; m != head; m = m.next) a[i++] = m.name;
    return a;
  }

  public double[] getPrices() {
    double[] a = new double[size];
    int i = 0;
    for (Model m = head.next; m != head; m = m.next) a[i++] = m.price;
    return a;
  }

  public double getPrice(String n) throws NoSuchModelNameException {
    return require(n).price;
  }

  public void setPrice(String n, double p) throws NoSuchModelNameException {
    Model m = require(n);
    Validation.price(p);
    m.price = p;
    touch();
  }

  public void renameModel(String old, String n)
      throws NoSuchModelNameException, DuplicateModelNameException {
    Model m = require(old);
    Validation.name(n);
    Model other = find(n);
    if (other != null && other != m) throw new DuplicateModelNameException(n);
    m.name = n;
    touch();
  }

  public void addModel(String n, double p) throws DuplicateModelNameException {
    Validation.name(n);
    Validation.price(p);
    if (find(n) != null) throw new DuplicateModelNameException(n);
    append(n, p);
    touch();
  }

  public void removeModel(String n) throws NoSuchModelNameException {
    Model m = require(n);
    m.prev.next = m.next;
    m.next.prev = m.prev;
    size--;
    touch();
  }
}
