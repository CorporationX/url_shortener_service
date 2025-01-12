package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${url.hash.generate-count}")
    private int hashGenerateCount;

    @Async("urlHashTaskExecutor")
    @Transactional
    public void generateBatch() {
        log.info("Generating new {} hashs for urls...", hashGenerateCount);
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(hashGenerateCount);
        List<String> urlHashs = base62Encoder.encode(uniqueNumbers);
        hashRepository.save(urlHashs);
        log.info("Finished generating new {} hashs for urls!", hashGenerateCount);
    }
}
