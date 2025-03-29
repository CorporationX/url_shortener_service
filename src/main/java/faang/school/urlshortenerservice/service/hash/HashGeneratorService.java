package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final HashService hashService;

    @Transactional
    public void generateBatch(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize должен быть больше нуля");
        }
        List<String> hashesStrings = base62Encoder.encode(hashRepository.getUniqueNumbers(batchSize));
        hashService.saveHashes(hashesStrings);
    }
}
