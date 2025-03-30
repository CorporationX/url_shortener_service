package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    @Value("${hashGenerator.batchSize}")
    private final int batchSize;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public void generateHashes() {
        try {
            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.info("Fetched {} unique numbers.", uniqueNumbers.size());

            if (!uniqueNumbers.isEmpty()) { // Добавляем проверку на пустоту
                List<String> hashes = base62Encoder.encode(uniqueNumbers);
                log.info("Generated {} hashes.", hashes.size());
                hashRepository.saveAllBatch(hashes);
            } else {
                log.warn("No unique numbers received from repository");
            }

//            List<String> hashes = base62Encoder.encode(uniqueNumbers);
//            log.info("Generated {} hashes.", hashes.size());

            //hashRepository.saveAllBatch(hashes);
        } catch (Exception ex) {
            log.error("Error generating hash batch", ex);
        }
    }
}
