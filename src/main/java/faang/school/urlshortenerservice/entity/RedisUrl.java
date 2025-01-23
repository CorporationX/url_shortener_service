package faang.school.urlshortenerservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

/**
 * Сущность, представляющая URL в Redis.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Url")
public class RedisUrl {

    /**
     * Уникальный идентификатор записи в Redis.
     */
    @Id
    private Long id;

    /**
     * Хэш, представляющий собой короткий URL.
     */
    @Getter
    private String hash;

    /**
     * Полный URL, который был сокращён.
     */
    private String url;

    /**
     * Время жизни записи в Redis (в днях).
     */
    @TimeToLive(unit = TimeUnit.DAYS)
    private Long ttl = 1L;
}
