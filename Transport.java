package labs;

import java.io.Serializable;

public interface Transport extends Serializable {
  String getBrand();

  void setBrand(String brand);

  String[] getModels();

  double[] getPrices();

  int getSize();

  void renameModel(String oldName, String newName)
      throws NoSuchModelNameException, DuplicateModelNameException;

  double getPrice(String name) throws NoSuchModelNameException;

  void setPrice(String name, double price) throws NoSuchModelNameException;

  void addModel(String name, double price) throws DuplicateModelNameException;

  void removeModel(String name) throws NoSuchModelNameException;
}
