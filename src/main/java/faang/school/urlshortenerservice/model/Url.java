package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    private String hash;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

}
