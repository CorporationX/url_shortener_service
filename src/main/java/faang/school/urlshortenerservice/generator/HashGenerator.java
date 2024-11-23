package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Encoder encoder;

    @Value("${spring.jpa.amount-unique-numbers}")
    private int amountHash;

    @Transactional
    public void generateBatch() {
        List<Hash> hashes = encoder.encode(hashRepository.getUniqueNumbers(amountHash));
        hashRepository.saveAll(hashes);
    }
}