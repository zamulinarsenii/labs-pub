package labs;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "track", uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "title"}))
@Check(constraints = "length(trim(title)) > 0 and duration_seconds > 0")
public class Track {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "track_seq")
  @SequenceGenerator(name = "track_seq", sequenceName = "track_seq", allocationSize = 1)
  private Long id;

  public Long getId() {
    return id;
  }

  @ManyToOne(optional = false)
  @JoinColumn(name = "album_id", nullable = false)
  private Album album;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(name = "duration_seconds", nullable = false)
  private int durationSeconds;

  public Track() {}

  public Track(Album a, String t, int d) {
    album = a;
    title = t;
    durationSeconds = d;
  }

  public Album getAlbum() {
    return album;
  }

  public void setAlbum(Album a) {
    album = a;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String s) {
    title = s;
  }

  public int getDurationSeconds() {
    return durationSeconds;
  }

  public void setDurationSeconds(int n) {
    durationSeconds = n;
  }
}
