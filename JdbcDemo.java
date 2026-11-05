package labs;

import java.nio.file.*;
import java.sql.*;

public class JdbcDemo {
  public static Connection connect() throws SQLException {
    return DriverManager.getConnection(
        System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/music_store"),
        System.getenv().getOrDefault("DB_USER", "lab"),
        System.getenv().getOrDefault("DB_PASSWORD", "lab"));
  }

  public static void main(String[] args) throws Exception {
    try (Connection c = connect()) {
      if (args.length > 0 && args[0].equals("--init")) {
        c.setAutoCommit(false);
        try {
          for (String f : new String[] {"01_schema.sql", "02_seed.sql"})
            try (Statement st = c.createStatement()) {
              st.execute(Files.readString(Path.of("lab06/sql", f)));
            }
          c.commit();
        } catch (Exception e) {
          c.rollback();
          throw e;
        } finally {
          c.setAutoCommit(true);
        }
      }
      String queries = Files.readString(Path.of("lab06/sql/03_queries.sql"));
      for (String q : queries.split(";")) {
        if (q.isBlank()) continue;
        try (Statement s = c.createStatement();
            ResultSet r = s.executeQuery(q)) {
          while (r.next())
            System.out.printf("%s | %s | %d s%n", r.getString(1), r.getString(2), r.getInt(3));
        }
      }
      c.setAutoCommit(false);
      try {
        long id;
        try (PreparedStatement p =
            c.prepareStatement("INSERT INTO artist(name) VALUES (?) RETURNING id")) {
          p.setString(1, "JDBC demo " + java.util.UUID.randomUUID());
          try (ResultSet r = p.executeQuery()) {
            r.next();
            id = r.getLong(1);
          }
        }
        try (PreparedStatement p = c.prepareStatement("UPDATE artist SET name=? WHERE id=?")) {
          p.setString(1, "Edited " + id);
          p.setLong(2, id);
          System.out.println("Updated: " + p.executeUpdate());
        }
        try (PreparedStatement p = c.prepareStatement("DELETE FROM artist WHERE id=?")) {
          p.setLong(1, id);
          System.out.println("Deleted: " + p.executeUpdate());
        }
        c.commit();
      } catch (Exception e) {
        c.rollback();
        throw e;
      }
    }
  }
}
