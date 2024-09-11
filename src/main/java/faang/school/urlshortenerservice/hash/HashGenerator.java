package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final HashProperties properties;
    private final Base62Encoder encoder;

    @Async(value = "taskExecutor")
    @Transactional
    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(properties.getUniqueNumbersSize());
        List<Hash> hashes = encoder.encodeList(uniqueNumbers);
        hashRepository.saveHashes(hashes);
    }
}
