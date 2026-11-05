package labs;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;
import org.hibernate.*;

@WebServlet("/store")
public class StoreServlet extends HttpServlet {
  private SessionFactory factory() {
    return (SessionFactory) getServletContext().getAttribute("factory");
  }

  private static String text(HttpServletRequest r, String k, int max) {
    String s = r.getParameter(k);
    if (s == null || s.isBlank() || s.length() > max)
      throw new IllegalArgumentException("Проверьте поле " + k);
    return s.trim();
  }

  private static long id(HttpServletRequest r, String k) {
    long n = Long.parseLong(r.getParameter(k));
    if (n <= 0) throw new IllegalArgumentException("Некорректный идентификатор");
    return n;
  }

  private static <T> T entity(Session s, Class<T> type, long id) {
    T e = s.find(type, id);
    if (e == null) throw new IllegalArgumentException("Запись не найдена");
    return e;
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    if (req.getSession().getAttribute("csrf") == null)
      req.getSession().setAttribute("csrf", UUID.randomUUID().toString());
    try (Session s = factory().openSession()) {
      req.setAttribute("artists", s.createQuery("from Artist order by id", Artist.class).list());
      req.setAttribute(
          "albums",
          s.createQuery("from Album a join fetch a.artist order by a.id", Album.class).list());
      req.setAttribute(
          "tracks",
          s.createQuery("from Track t join fetch t.album order by t.id", Track.class).list());
      req.setAttribute(
          "shortest",
          s.createQuery(
                  "select a.title,t.title,t.durationSeconds from Album a, Track t where t.album=a"
                      + " and (select count(x) from Track x where x.album=a)>=5 and"
                      + " t.durationSeconds=(select min(y.durationSeconds) from Track y where"
                      + " y.album=a) order by a.id,t.id",
                  Object[].class)
              .list());
      req.setAttribute(
          "stats",
          s.createQuery(
                  "select ar.name,count(t.id),coalesce(sum(t.durationSeconds),0) from Artist ar"
                      + " left join Album a on a.artist=ar left join Track t on t.album=a group by"
                      + " ar.id,ar.name order by ar.name",
                  Object[].class)
              .list());
      String kind = req.getParameter("kind"), edit = req.getParameter("edit");
      if (edit != null) {
        long key = Long.parseLong(edit);
        if ("artist".equals(kind)) req.setAttribute("editArtist", entity(s, Artist.class, key));
        else if ("album".equals(kind)) req.setAttribute("editAlbum", entity(s, Album.class, key));
        else if ("track".equals(kind)) req.setAttribute("editTrack", entity(s, Track.class, key));
      }
      req.getRequestDispatcher("/WEB-INF/store.jsp").forward(req, res);
    } catch (IllegalArgumentException e) {
      res.sendError(400, "Некорректные параметры");
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    Object csrf = req.getSession().getAttribute("csrf");
    if (csrf == null || !csrf.equals(req.getParameter("csrf"))) {
      res.sendError(403);
      return;
    }
    try (Session s = factory().openSession()) {
      Transaction tx = s.beginTransaction();
      try {
        String kind = req.getParameter("kind"), action = req.getParameter("action");
        boolean add = "add".equals(action), delete = "delete".equals(action);
        if (!add && !delete && !"update".equals(action))
          throw new IllegalArgumentException("Неизвестное действие");
        if ("artist".equals(kind)) {
          Artist a = add ? new Artist() : entity(s, Artist.class, id(req, "id"));
          if (delete) s.remove(a);
          else {
            a.setName(text(req, "name", 150));
            if (add) s.persist(a);
          }
        } else if ("album".equals(kind)) {
          Album a = add ? new Album() : entity(s, Album.class, id(req, "id"));
          if (delete) s.remove(a);
          else {
            a.setTitle(text(req, "title", 150));
            a.setGenre(text(req, "genre", 80));
            a.setArtist(entity(s, Artist.class, id(req, "artistId")));
            if (add) s.persist(a);
          }
        } else if ("track".equals(kind)) {
          Track t = add ? new Track() : entity(s, Track.class, id(req, "id"));
          if (delete) s.remove(t);
          else {
            t.setTitle(text(req, "title", 150));
            int duration = Integer.parseInt(req.getParameter("duration"));
            if (duration <= 0)
              throw new IllegalArgumentException("Длительность должна быть положительной");
            t.setDurationSeconds(duration);
            t.setAlbum(entity(s, Album.class, id(req, "albumId")));
            if (add) s.persist(t);
          }
        } else throw new IllegalArgumentException("Неизвестная таблица");
        tx.commit();
        req.getSession().setAttribute("message", "Изменения сохранены");
      } catch (RuntimeException e) {
        if (tx.isActive()) tx.rollback();
        req.getSession()
            .setAttribute(
                "message",
                e instanceof IllegalArgumentException
                    ? e.getMessage()
                    : "Операция отклонена. Проверьте уникальность и связанные записи. Сначала"
                          + " удалите дочерние записи.");
      }
    }
    res.sendRedirect(req.getContextPath() + "/store");
  }
}
