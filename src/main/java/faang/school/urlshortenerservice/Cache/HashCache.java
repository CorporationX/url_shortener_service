package faang.school.urlshortenerservice.Cache;

import faang.school.urlshortenerservice.HashGenerator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashCache {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;

    @Value("${hash.cache.size}")
    private int cacheSize;
    @Value("${hash.cache.threshold}")
    private double cacheThreshold;

}
