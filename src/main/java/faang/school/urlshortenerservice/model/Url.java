package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "urls")
public class Url {
    @Id
    @Column(name = "hash_value", nullable = false, unique = true)
    private String hashValue;

    @Column(name = "url_value", nullable = false)
    private String urlValue;

    @Column(name = "validated_at", nullable = false)
    private LocalDateTime validatedAt;

    @PrePersist
    private void setValidatedAt() {
        this.validatedAt = LocalDateTime.now().plusYears(1);
    }
}