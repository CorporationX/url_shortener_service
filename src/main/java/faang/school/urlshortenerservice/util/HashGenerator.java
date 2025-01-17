package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import jdk.dynalink.linker.LinkerServices;
import lombok.AllArgsConstructor;
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
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${hash.sequence-amount")
    private int batchSize;

    @Async("hashGeneratorExecutor")
    public void generateBatch() {
        try {

            List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);
            log.info("sequence numbers to be hashed: {}", uniqueNumbers);
            List<String> hashes = base62Encoder.encode(uniqueNumbers);

            hashRepository.save(hashes);
        } catch (Exception e) {
            log.error("error during generating hashcode :", e);
            throw new RuntimeException(e);
        }
    }
}
