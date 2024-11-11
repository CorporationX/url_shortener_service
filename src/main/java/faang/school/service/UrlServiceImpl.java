package faang.school.service;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long N = 100; // Порог обращений для кэширования
    private static final long CACHE_DURATION = 24; // Время хранения записи в кэше в часах
    private static final long TTL_INCREMENT = 1; // Увеличение TTL в минутах
    private static final long NOVICE_PROTECTION_TIME = 60 * 60 * 1000; // 1 час в миллисекундах
    private static final String PROTECTED_BEGINNERS = "PROTECTED_BEGINNERS";
    private static final String HASH_BY_URLS = "HASH_BY_URLS";

    @Override
    public String redirectByHash(String hash) {
        String beginner = (String) redisTemplate.opsForHash().get(PROTECTED_BEGINNERS, hash);

        if (beginner != null) {
            return beginner;
        }

        Long count = redisTemplate.opsForValue().increment(hash);
        String url = (String) redisTemplate.opsForHash().get(HASH_BY_URLS, hash);

        if (count != null && count >= N) {
            if (url != null) {
                redisTemplate.delete(hash);
                return url;
            } else {
                redisTemplate.opsForHash().put(HASH_BY_URLS, hash, getUrlBy(hash));
                return url;
            }
        } else {
            return getUrlBy(hash);
        }

        // Проверка, достиг ли счетчик порога
        if (count != null && count >= N) {
            // Извлекаем значение из базы данных и кэшируем его на 24 часа
            String valueFromDb = getStringFromDatabase(hash);
            redisTemplate.opsForValue().set(cacheKey, valueFromDb, CACHE_DURATION, TimeUnit.HOURS);

            // Сохраняем время начала защиты новичка
            redisTemplate.opsForValue().set(noviceProtectionKey, System.currentTimeMillis());

            return valueFromDb;
        }

        // Если порог не достигнут, возвращаем значение из базы данных напрямую
        return getStringFromDatabase(hash);
    }

    private String getUrlBy(String hash) {
        return urlRepository.findById(hash)
                .map(Url::getHash)
                .orElseThrow(() -> new EntityNotFoundException("Url with hash %s not found".formatted(hash)));
    }

    private String getStringFromDatabase(Long recordId) {
        // Имитация получения строки из базы данных по идентификатору записи
        return "String from Database for ID " + recordId;
    }
}
