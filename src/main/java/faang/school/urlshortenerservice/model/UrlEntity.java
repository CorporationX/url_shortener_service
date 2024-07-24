package faang.school.urlshortenerservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "url")
public class UrlEntity {
    @Id
    private Long id;
    private String hash;
    private String longUrl;
}
