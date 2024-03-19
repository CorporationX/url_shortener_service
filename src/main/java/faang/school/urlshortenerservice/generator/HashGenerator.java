package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("treadPool")
    public void generateBatch(int numberOfValue) {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(numberOfValue);
        List<String> strings = base62Encoder.encodeListOfNumbers(uniqueNumbers);
        hashRepository.save(strings);
    }
}
