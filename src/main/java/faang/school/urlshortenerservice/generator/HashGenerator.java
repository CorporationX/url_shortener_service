package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final Base62Encoder encoder;
    private final HashRepository hashRepository;

    @Value("${spring.url-shortener.hash.number-of-generations}")
    private long quantity;

    public void generateBatch() {
     List<Long> numbers = hashRepository.getUniqueNumbers(quantity);
     List<Hash> hashes = encoder.encode(numbers);

     hashRepository.saveAll(hashes);
    }
}
