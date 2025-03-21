package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Url {

    @Id
    private String hash;
    private String url;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
