package labs;

import jakarta.persistence.*;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "artist")
@Check(constraints = "length(trim(name)) > 0")
public class Artist {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "artist_seq")
  @SequenceGenerator(name = "artist_seq", sequenceName = "artist_seq", allocationSize = 1)
  private Long id;

  public Long getId() {
    return id;
  }

  @Column(nullable = false, unique = true, length = 150)
  private String name;

  public Artist() {}

  public Artist(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
