package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.crypto.BaseEncoder;
import faang.school.urlshortenerservice.service.HashService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final BaseEncoder baseEncoder;
    private final HashService hashService;

    @Async("taskExecutor")
    public void generateBatch(int hashBatch) {
        List<Long> numbers = hashService.getUniqueNumbers(hashBatch);
        List<String> hashes = baseEncoder.encode(numbers);
        hashService.saveBatch(hashes);
        log.info("Generated {} hashes", hashes.size());
    }
}
