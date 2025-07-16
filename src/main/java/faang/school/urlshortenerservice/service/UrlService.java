package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.Base62Encoder;
import faang.school.urlshortenerservice.HashGenerator;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.JdbcHashRepository;
import faang.school.urlshortenerservice.repository.JdbcUrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {
    private final HashGenerator hashGenerator;
    private final JdbcHashRepository jdbcHashRepository;
    private final JdbcUrlRepository urlRepository;
    @Value("${base-url}")
    private String baseUrl;

    @Transactional
    public String generateShortUrl(String longUrl) {
        List<String> batch = jdbcHashRepository.getHashBatch();

        if (batch.isEmpty()) {
            hashGenerator.generateBatch();
            batch = jdbcHashRepository.getHashBatch();

            if (batch.isEmpty()) {
                throw new IllegalArgumentException("No hash available for url shortening");
            }
        }
        String hash = batch.get(0);

        urlRepository.save(hash, longUrl, LocalDateTime.now());
        return baseUrl + "/" + hash;

    }

}
