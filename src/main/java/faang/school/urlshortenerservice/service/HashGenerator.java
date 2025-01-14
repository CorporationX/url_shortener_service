package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.properties.short_url.ShortUrlProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final ShortUrlProperties shortUrlProperties;

    @Async("urlHashTaskExecutor")
    @Transactional
    public void generateBatch() {
        int newHashCount = shortUrlProperties.getHashGenerationSettings().getDbCreateBatchSize();
        log.info("Generating new {} hashs for urls...", newHashCount);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(newHashCount);
        List<String> urlHashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(urlHashes);
        log.info("Finished generating new {} hashs for urls!", newHashCount);
    }
}
