package faang.school.urlshortenerservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
@Builder
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
public class Url {
    @Id
    @NotBlank(message = "Hash cannot be blank.")
    private String hash;

    @URL(message = "URL must be valid.")
    @Column(name = "url", nullable = false)
    private String url;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
