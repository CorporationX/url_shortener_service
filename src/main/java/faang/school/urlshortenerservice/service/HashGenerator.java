package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder encoder;
    @Value("${hash.range}")
    private int maxRange;

    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(maxRange);
        hashRepository.saveAll(encoder.encode(uniqueNumbers));
    }
}
