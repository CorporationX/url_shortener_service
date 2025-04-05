package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final HashRepository hashRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")//"${hash:cron}")
    public void generateBatch() {
        List<Long> range = hashRepository.getUniqueNumbers(maxRange);
        List<Hash> hashes = range.stream()
                .map(this::applyBase62Encoding)
                        .map(Hash::new)
                                .toList();

        hashRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if(hashes.size() < amount) {
            generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount) {

        return CompletableFuture.completedFuture(getHashes(amount));
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
