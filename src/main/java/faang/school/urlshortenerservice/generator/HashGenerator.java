package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${app.hash.maxRange}")
    private int maxRange;

    @Transactional
    public List<String> generateHashes() { //TODO можно параллельность добавить.
        List<Long> range = hashRepository.getNextRange(maxRange);

        List<Hash> hashes = base62Encoder.encode(range).stream()
                .map(Hash::new)
                .toList();

        hashRepository.saveAll(hashes);

        log.info("Generated and saved {} hashes", hashes.size());
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async(value = "hashGeneratorExecutor")
    public CompletableFuture<List<String>> generateHashesAsync() {
        return CompletableFuture.completedFuture(generateHashes());
    }


    @Transactional
    @Async("hashGeneratorExecutor") // TODO стоит подумать как сделать так небыло такой ситуации чтоб в двух сервисах одинаковых был вызван этот метод в разных потоках.
    public CompletableFuture<List<Hash>> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateHashes();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return CompletableFuture.completedFuture(hashes);
    }
}