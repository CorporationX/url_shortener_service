package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "url")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Url {

    @Id
    @Column(name = "hash", length = 6)
    private String hash;

    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
