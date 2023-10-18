package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private static final String BASE_62_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final UrlRepository urlRepository;

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    public void generateHash() {
        List<Long> range = urlRepository.getNextRange(maxRange);
        List<Hash> hashes = range.stream()
                .map(this::applyBase62Encoding)
                .map(Hash::new)
                .toList();
        urlRepository.saveAll(hashes);
    }

    @Transactional
    public List<String> getHashes(long amount){
        List<Hash> hashes = urlRepository.findAndDelete(amount);
        if (hashes.size() < amount){
            generateHash();
            hashes.addAll(urlRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }

    @Async("hashGeneratorExecutor")
    public CompletableFuture<List<String>> getHashesAsync(long amount){
        return CompletableFuture.completedFuture(getHashes(amount));
    }

    private String applyBase62Encoding(long number){
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0){
            stringBuilder.append(BASE_62_CHARS.charAt((int) (number % BASE_62_CHARS.length())));
            number /= BASE_62_CHARS.length();
        }
        return stringBuilder.toString();
    }
}
