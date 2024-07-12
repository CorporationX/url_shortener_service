package faang.school.urlshortenerservice.service.generator;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Data
public class HashGenerator {

    @Value("${hash-generator.unique-numbers.size}")
    private long uniqueNumbers;

    private final HashRepository hashRepository;

    private final Base62Encoder base62Encoder;

    @Async("getThreadPool")
    public void generateBatch(long uniqueNumbers) {
        hashRepository.save(base62Encoder.encode(hashRepository.getUniqueNumbers(uniqueNumbers)));
    }
}
