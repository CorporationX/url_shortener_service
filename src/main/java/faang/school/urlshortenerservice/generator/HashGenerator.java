package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.hash.Base62Encoder;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository repository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    public void generateHash() {
        generateHash(maxRange);
    }

    @Transactional
    public void generateHash(long count) {
        List<Long> range = repository.getNextRange(count);
        List<Hash> hashes = range.stream()
                .map(base62Encoder::applyBase62Encoding)
                .map(Hash::new)
                .toList();
        repository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = repository.findAndDelete(amount);

        while (hashes.size() < amount) {
            long toGenerate = amount - hashes.size();
            generateHash(toGenerate);
            hashes.addAll(repository.findAndDelete(toGenerate));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }
}



