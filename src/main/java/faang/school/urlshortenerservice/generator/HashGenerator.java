package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${custom.batch-size}")
    private int bathSize;

    public HashGenerator(HashRepository hashRepository, Base62Encoder base62Encoder) {
        this.hashRepository = hashRepository;
        this.base62Encoder = base62Encoder;
    }

    @Async("hashGeneratorThreadPool")
    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(bathSize);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveBatch(hashes);
    }
}
