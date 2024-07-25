package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity(name = "url")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @NotNull
    private String hash;

    @Column(name = "url", nullable = false)
    @NotNull
    @Size(max = 512)
    private String url;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "last_received_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastReceivedAt;
}