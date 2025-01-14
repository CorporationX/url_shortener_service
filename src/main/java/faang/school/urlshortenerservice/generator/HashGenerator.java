package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private static final String BASE_62_CHARACTER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final HashRepository hashRepository;

    private final EntityManager entityManager;

    @Value("${hash.range}")
    private int maxRange;

    @Async("hashGeneratorExecutor")
    @Transactional
    public void generateBatch() {
        List<Long> range = hashRepository.getNextRange(maxRange);
        List<Hash> hashes = range.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
        hashRepository.saveAllAndFlush(hashes);
        entityManager.clear();
    }

    private String applyBase62Encoding(long number) {
        StringBuilder result = new StringBuilder();
        while (number > 0) {
            result.append(BASE_62_CHARACTER.charAt((int) (number % BASE_62_CHARACTER.length())));
            number /= BASE_62_CHARACTER.length();
        }
        return result.toString();
    }

}
