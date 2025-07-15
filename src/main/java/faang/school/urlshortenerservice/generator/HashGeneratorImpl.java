package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGeneratorImpl implements HashGenerator {
    private final HashRepository hashRepository;

    private static final String BASE_62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    @Value("${hashRange.maximum}")
    private int maximumHashRange;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Transactional
    public void generateHash() {
        List<Long> range = hashRepository.getNextRange(maximumHashRange);
        List<Hash> hashes = range.stream()
                .map(this::applyBase62)
                .map(Hash::new)
                .toList();
        for (int i = 0; i < hashes.size(); i += batchSize) {
            List<Hash> part = hashes.subList(i, Math.min(i + batchSize, hashes.size()));
            hashRepository.saveAll(part);
            hashRepository.flush();
        }
    }

    @Transactional
    public List<String> fetchHashes(int amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHash();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream()
                .map(Hash::getActualHash)
                .toList();
    }

    private String applyBase62(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(
                    BASE_62.charAt((int) (number % BASE_62.length()))
            );
            number /= BASE_62.length();
        }
        return builder.toString();
    }
}
