package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Builder
@Table(name = "url")
@AllArgsConstructor
@NoArgsConstructor
public class Url implements Serializable {

    @Id
    private String hash;

    @Column(name = "url", unique = true, nullable = false)
    private String url;

}
