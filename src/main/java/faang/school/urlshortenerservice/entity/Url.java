package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "url")
public class Url {

    @Id
    private String hash;

    @Column(nullable = false, length = 50)
    private String url;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
}