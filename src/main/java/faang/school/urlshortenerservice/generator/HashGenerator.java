package faang.school.urlshortenerservice.generator;

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

    @Value("spring.url-shortener.hash.number_of_generations")
    private long quantity;

    @Async("encodePool")
    public void generateBatch() {
     List<Long> numbers = hashRepository.getUniqueNumbers(quantity);
     List<String> hashes = encoder.encode(numbers);

     hashRepository.save(hashes);
    }

}
