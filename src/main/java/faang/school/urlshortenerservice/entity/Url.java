package faang.school.urlshortenerservice.entity;

import faang.school.urlshortenerservice.common.Identifiable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "url")
public class Url extends Identifiable implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "hash", length = 6, nullable = false)
    private String hash;

    @Column(name = "url", nullable = false)
    private String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
