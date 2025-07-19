package faang.school.urlshortenerservice.util.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.HashGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder

    @Value("${hash.generate_batch_size}")
    private final int generateBatchSize;
    @Override
    public void generateBatch() {
        hashRepository.getUniqueNumbers(generateBatchSize);

    }
}
