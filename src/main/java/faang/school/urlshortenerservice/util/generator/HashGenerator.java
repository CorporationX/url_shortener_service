package faang.school.urlshortenerservice.util.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.encoder.Encoder;
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

    @Value("${spring.hash.generator.amount-hash}")
    private int amountHash;

    @Transactional
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(amountHash);
        List<Hash> hashes = encoder.encodeBatch(uniqueNumbers);
        hashRepository.saveAll(hashes);
    }
}
