package faang.school.urlshortenerservice.generator.hash;

import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.uniquenumber.UniqueNumber;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashService hashService;
    private final Base62Encoder base62Encoder;
    private final UniqueNumber uniqueNumber;

    @Setter
    @Value("${value.quantityBath}")
    private int quantity;

    @Async("generateBatchPool")
    public void generateBatch() {
        log.info("Starting the hash creation process");
        List<Long> numbers = uniqueNumber.getUniqueNumbers(quantity);
        log.debug("Received list of numbers from DB {}", numbers);
        List<String> hashList = base62Encoder.encode(numbers);
        log.debug("Received list of hash {}", hashList);
        hashService.saveHashes(hashList);
        log.info("The caches were successfully saved to the database");
    }
}
