package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {
    private final Base62Converter base62Converter;
    private final HashRepository hashRepository;

    @Value("${hash.max-range}")
    private int maxRange;

    @Transactional
    public synchronized List<String> getHashBatch(long amount) {
        List<Hash> hashes = new ArrayList<>();
        while (hashes.size() < amount) {
            List<Hash> newHashes = hashRepository.findAndDelete(amount);
            hashes.addAll(newHashes);
            if (hashes.size() < amount) {
                generateHash();
            }
        }

        return hashes.stream()
                .map(Hash::getHash)
                .toList();
    }

    private void generateHash() {
        List<Long> numbers = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = numbers.stream()
                .map(num -> new Hash(base62Converter.convertToBase62(num)))
                .toList();

        hashRepository.saveAll(hashes);
    }
}
