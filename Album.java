package labs;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "album", uniqueConstraints = @UniqueConstraint(columnNames = {"artist_id", "title"}))
@Check(constraints = "length(trim(title)) > 0 and length(trim(genre)) > 0")
public class Album {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_seq")
  @SequenceGenerator(name = "album_seq", sequenceName = "album_seq", allocationSize = 1)
  private Long id;

  public Long getId() {
    return id;
  }

  @ManyToOne(optional = false)
  @JoinColumn(name = "artist_id", nullable = false)
  private Artist artist;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(nullable = false, length = 80)
  private String genre;

  public Album() {}

  public Album(Artist a, String t, String g) {
    artist = a;
    title = t;
    genre = g;
  }

  public Artist getArtist() {
    return artist;
  }

  public void setArtist(Artist a) {
    artist = a;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String s) {
    title = s;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String s) {
    genre = s;
  }
}
