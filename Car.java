package labs;

import java.io.Serializable;
import java.util.Arrays;

public class Car implements Transport {
  private static final long serialVersionUID = 1L;
  private String brand;

  private class Model implements Serializable {
    private static final long serialVersionUID = 1L;
    String name;
    double price;

    Model(String name, double price) {
      this.name = name;
      this.price = price;
    }
  }

  private Model[] models;

  public Car(String brand, int size) {
    this.brand = Validation.name(brand);
    Validation.size(size);
    models = new Model[size];
    for (int i = 0; i < size; i++) models[i] = new Model("Model" + (i + 1), 0);
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String b) {
    brand = Validation.name(b);
  }

  private int find(String n) {
    for (int i = 0; i < models.length; i++) if (models[i].name.equals(n)) return i;
    return -1;
  }

  private int require(String n) throws NoSuchModelNameException {
    int i = find(n);
    if (i < 0) throw new NoSuchModelNameException(n);
    return i;
  }

  public void renameModel(String old, String n)
      throws NoSuchModelNameException, DuplicateModelNameException {
    int i = require(old);
    Validation.name(n);
    int j = find(n);
    if (j >= 0 && j != i) throw new DuplicateModelNameException(n);
    models[i].name = n;
  }

  public String[] getModels() {
    return Arrays.stream(models).map(m -> m.name).toArray(String[]::new);
  }

  public double[] getPrices() {
    return Arrays.stream(models).mapToDouble(m -> m.price).toArray();
  }

  public double getPrice(String n) throws NoSuchModelNameException {
    return models[require(n)].price;
  }

  public void setPrice(String n, double p) throws NoSuchModelNameException {
    int i = require(n);
    Validation.price(p);
    models[i].price = p;
  }

  public void addModel(String n, double p) throws DuplicateModelNameException {
    Validation.name(n);
    Validation.price(p);
    if (find(n) >= 0) throw new DuplicateModelNameException(n);
    models = Arrays.copyOf(models, models.length + 1);
    models[models.length - 1] = new Model(n, p);
  }

  public void removeModel(String n) throws NoSuchModelNameException {
    int i = require(n);
    System.arraycopy(models, i + 1, models, i, models.length - i - 1);
    models = Arrays.copyOf(models, models.length - 1);
  }

  public int getSize() {
    return models.length;
  }
}
