package labs;

import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.util.*;
import org.hibernate.*;

@WebServlet("/suggest")
public class SuggestServlet extends HttpServlet {
  static String json(String s) {
    StringBuilder b = new StringBuilder("\"");
    for (char c : s.toCharArray()) {
      switch (c) {
        case '"':
          b.append("\\\"");
          break;
        case '\\':
          b.append("\\\\");
          break;
        default:
          if (c < 32) b.append(String.format("\\u%04x", (int) c));
          else b.append(c);
      }
    }
    return b.append('"').toString();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("application/json;charset=UTF-8");
    String kind = req.getParameter("kind"), q = req.getParameter("q");
    if (q == null) q = "";
    if (q.length() > 150) {
      res.sendError(400);
      return;
    }
    String hql =
        switch (kind == null ? "" : kind) {
          case "artist" -> "select name from Artist where lower(name) like :q order by name";
          case "album" ->
              "select distinct title from Album where lower(title) like :q order by title";
          case "track" ->
              "select distinct title from Track where lower(title) like :q order by title";
          case "genre" ->
              "select distinct genre from Album where lower(genre) like :q order by genre";
          default -> null;
        };
    if (hql == null) {
      res.sendError(400);
      return;
    }
    try (Session s = ((SessionFactory) getServletContext().getAttribute("factory")).openSession()) {
      List<String> values =
          s.createQuery(hql, String.class)
              .setParameter("q", q.toLowerCase(Locale.ROOT) + "%")
              .setMaxResults(10)
              .list();
      res.getWriter()
          .write("[" + String.join(",", values.stream().map(SuggestServlet::json).toList()) + "]");
    }
  }
}
