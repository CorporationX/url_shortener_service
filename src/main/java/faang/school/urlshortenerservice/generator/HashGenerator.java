package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash.batch}")
    private String batchHash;

    @Value("${app.hash.maxRange}")
    private int maxRange;

    public void generateHash() {

    }

    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getNextRange(maxRange);

        List<Hash> hashes = range.stream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);
     }
}
