package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "hash")
public class Hash {

  @Id
  @Column(name = "hash", unique = true, length = 6, nullable = false)
  private String hash;
}