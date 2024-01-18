package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final HashRepository hashRepository;

    @Value("${spring.hash.range:100000}")
    private int maxRange;

    @Async("hashGeneratorExecutor")
    @Transactional
    public CompletableFuture<Void> generateHash() {
        List<Long> range = hashRepository.generateBatch(maxRange);
        List<HashEntity> hashes = range.stream()
                .map(this::applyBase62Encoding)
                .map(HashEntity::new)
                .toList();
        hashRepository.saveAll(hashes);
        return CompletableFuture.completedFuture(null);
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return builder.toString();
    }
}
