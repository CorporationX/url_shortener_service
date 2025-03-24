package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.service.hash.api.HashCache;
import faang.school.urlshortenerservice.service.hash.api.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {
    private final HashCache hashCache;

    @PostConstruct
    public void initializing() {
        log.info("Initializing Cache");
        hashCache.ensureCacheIsFilled();
        log.info("Cache initialized successfully");
    }
}
