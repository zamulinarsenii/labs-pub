package labs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

class TransportTest {
  @Test
  void bothImplementations() throws Exception {
    for (Transport t : new Transport[] {new Car("A", 0), new Motorcycle("B", 0)}) {
      assertEquals(0, t.getSize());
      t.addModel("X", 10);
      t.addModel("Y", 30);
      assertEquals(20, TransportUtils.average(t));
      assertThrows(DuplicateModelNameException.class, () -> t.addModel("X", 1));
      assertThrows(NoSuchModelNameException.class, () -> t.getPrice("Z"));
      assertThrows(ModelPriceOutOfBoundsException.class, () -> t.setPrice("X", Double.NaN));
      assertThrows(ModelPriceOutOfBoundsException.class, () -> t.setPrice("X", -1));
      t.renameModel("X", "Z");
      t.renameModel("Z", "Z");
      assertThrows(DuplicateModelNameException.class, () -> t.renameModel("Z", "Y"));
      String[] names = t.getModels();
      names[0] = "bad";
      assertEquals("Z", t.getModels()[0]);
      t.removeModel("Z");
      t.removeModel("Y");
      assertEquals(0, t.getSize());
      t.addModel("new", 1);
      assertEquals(1, t.getPrice("new"));
    }
  }

  @Test
  void constructors() {
    assertArrayEquals(new String[] {"Model1", "Model2"}, new Car("A", 2).getModels());
    assertEquals(2, new Motorcycle("A", 2).getSize());
    assertThrows(IllegalArgumentException.class, () -> new Car("A", -1));
    assertThrows(IllegalArgumentException.class, () -> TransportUtils.average(new Car("A", 0)));
  }
}
