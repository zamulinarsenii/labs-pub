package labs;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
  private HibernateUtil() {}

  public static SessionFactory build(boolean reset) {
    Configuration c = new Configuration().configure();
    String[][] vars = {
      {"DB_URL", "hibernate.connection.url"},
      {"DB_USER", "hibernate.connection.username"},
      {"DB_PASSWORD", "hibernate.connection.password"}
    };
    for (String[] v : vars) {
      String value = System.getenv(v[0]);
      if (value != null) c.setProperty(v[1], value);
    }
    if (reset) c.setProperty("hibernate.hbm2ddl.auto", "create");
    return c.buildSessionFactory();
  }
}
