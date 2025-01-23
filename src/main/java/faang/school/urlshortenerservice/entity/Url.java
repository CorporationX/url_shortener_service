package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Сущность, представляющая URL в базе данных.
 */
@Entity
@Table(name = "url")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Url {

    /**
     * Уникальный идентификатор URL.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Хэш, представляющий собой короткий URL.
     */
    @Column(name = "hash", nullable = false, length = 6)
    private String hash;

    /**
     * Полный URL, который был сокращён.
     */
    @Column(name = "url", nullable = false)
    private String url;

    /**
     * Время создания записи. Заполняется автоматически при создании.
     */
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
