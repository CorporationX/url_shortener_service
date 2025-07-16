package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.Base62Encoder;
import faang.school.urlshortenerservice.HashCache;
import faang.school.urlshortenerservice.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.RedisRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;
    private final UrlRepository urlRepository;
    private final RedisRepository redisRepository;
    private final HashGenerator hashGenerator;
    private final HashCache hashCache;

    @Override
    @Transactional
    public String shortAndReturn(String longUrl) {
        String hash = hashCache.getHash();
        urlRepository.save(hash, longUrl);
        redisRepository.put(hash, longUrl);
        return hash;
    }
}
