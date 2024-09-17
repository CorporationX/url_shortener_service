package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.service.HashService;
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

    private final HashService hashService;
    private final Base62Encoder encoder;

    @Value("${spring.data.unique_numbers_number}")
    private int numberOfUniqueNumbers;

    @Async("executor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashService.getUniqueNumbers(numberOfUniqueNumbers);
        List<String> generatedHashes = encoder.encode(uniqueNumbers);
        hashService.saveAllHashes(generatedHashes);
    }
}
