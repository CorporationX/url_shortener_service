package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder encoder;

    @Value("${spring.jpa.amount-unique-numbers}")
    private int amountHash;

    @Transactional
    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void generateBatch() {
        List<Hash> hashes = encoder.encode(hashRepository.getUniqueNumbers(amountHash));
        hashRepository.saveAll(hashes);
    }
}
