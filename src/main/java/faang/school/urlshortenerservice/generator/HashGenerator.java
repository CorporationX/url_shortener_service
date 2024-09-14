package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.HashRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final HashRepositoryImpl hashRepositoryImpl;
    private final Base62Encoder encoder;

    @Value("${spring.data.unique_numbers_number}")
    private int numberOfUniqueNumbers;

    @Async("executor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(numberOfUniqueNumbers);
        List<String> generatedHashes = encoder.encode(uniqueNumbers);
        hashRepositoryImpl.saveHashes(generatedHashes);
    }
}
