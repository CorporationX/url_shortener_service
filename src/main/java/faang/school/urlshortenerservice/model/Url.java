package faang.school.urlshortenerservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

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

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "last_received_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastReceivedAt;
}
