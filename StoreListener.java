package labs;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import org.hibernate.SessionFactory;

@WebListener
public class StoreListener implements ServletContextListener {
  public void contextInitialized(ServletContextEvent e) {
    e.getServletContext().setAttribute("factory", HibernateUtil.build(false));
  }

  public void contextDestroyed(ServletContextEvent e) {
    Object f = e.getServletContext().getAttribute("factory");
    if (f instanceof SessionFactory) ((SessionFactory) f).close();
  }
}
