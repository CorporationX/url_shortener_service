package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "url")
public class Url {
    @Id
    @Column(name = "hash")
    String hash;

    @Column(name = "url")
    String url;
}
