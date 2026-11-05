package labs;

import org.hibernate.*;

public class HibernateDemo {
  public static void seed(Session s) {
    String[] names = {"Aurora Band", "North Wind", "Blue River", "City Lights", "Solar Trio"};
    for (int i = 0; i < 5; i++) {
      Artist artist = new Artist(names[i]);
      s.persist(artist);
      Album album = new Album(artist, "Album " + (i + 1), i % 2 == 0 ? "Rock" : "Jazz");
      s.persist(album);
      for (int n = 1; n <= (i == 4 ? 3 : 6); n++)
        s.persist(new Track(album, "Track " + n, n == 1 ? 3 : n <= 3 ? 180 : 180 + n * 10));
    }
  }

  public static void main(String[] args) {
    boolean reset = args.length == 1 && args[0].equals("--reset-lab-tables");
    try (SessionFactory factory = HibernateUtil.build(reset);
        Session s = factory.openSession()) {
      Transaction tx = s.beginTransaction();
      try {
        if (reset) seed(s);
        s.createQuery("from Album a join fetch a.artist order by a.id", Album.class)
            .getResultList()
            .forEach(a -> System.out.println(a.getTitle() + " / " + a.getArtist().getName()));
        Artist temp = new Artist("Demo " + java.util.UUID.randomUUID());
        s.persist(temp);
        s.flush();
        temp.setName("Edited " + temp.getId());
        s.flush();
        s.remove(temp);
        tx.commit();
        System.out.println("Hibernate CRUD completed");
      } catch (RuntimeException e) {
        tx.rollback();
        throw e;
      }
    }
  }
}
