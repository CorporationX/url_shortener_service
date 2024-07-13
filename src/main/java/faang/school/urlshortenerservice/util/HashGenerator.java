package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.repository.HashRepository;
import io.seruco.encoding.base62.Base62;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class HashGenerator {

    private HashRepository hashRepository;
    private Base62 base62;

    @Value("${hash.quantity-numbers.quantity}")
    private long quantity;

    public HashGenerator(HashRepository hashRepository) {
        this.hashRepository = hashRepository;
        base62 = Base62.createInstance();
    }

    @Transactional
    @Async("HashGeneratorThreadPool")
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(quantity);
        log.info("Generate unique numbers {}", numbers);

        List<String> hashes = encode(numbers);

        hashRepository.save(hashes);
        log.info("generate batch complete");
    }

    private List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(String::valueOf)
                .map(i -> new String(base62.encode(i.getBytes())))
                .toList();
    }
}
