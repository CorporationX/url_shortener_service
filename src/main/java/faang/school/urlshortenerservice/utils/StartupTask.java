package faang.school.urlshortenerservice.utils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StartupTask {

    private final HashGenerator hashGenerator;
    private final HashCacheService hashCacheService;

    @Value("${hash.generator.range-size}")
    private int rangeSize;

    @PostConstruct
    public void init() {
        int twentyPercentOfTotalHashes = (int) (rangeSize * 0.2);
        log.info("Checking if there are enough hashes");
        hashGenerator.generateAndSaveHashes(rangeSize);
        List<String> hashes = hashCacheService.fetchAndDeleteHashesFromDb(twentyPercentOfTotalHashes);
        hashCacheService.cacheHashes(hashes);
        log.info("Hashes generated and added to cache");
    }
}
