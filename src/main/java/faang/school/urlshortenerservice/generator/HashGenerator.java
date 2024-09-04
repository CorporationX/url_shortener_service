package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${custom.batch-size}")
    private final int bathSize;

    @Async("hashGeneratorThreadPool")
    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(bathSize);
        List<String> hashes = base62Encoder.encode(uniqueNumbers);
        hashRepository.saveBatch(hashes);
    }
}
