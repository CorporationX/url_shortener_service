package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final String base62Characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Value("${hash.generator.batch.size}")
    private int batchSize;

    @Async("hashTaskExecutor")
    public void generateBatch() {
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(batchSize);

        List<String> hashes = uniqueNumbers.stream()
                .map(this::applyBase62Encoding)
                .toList();

        hashRepository.saveBatch(hashes);
    }

    private String applyBase62Encoding(long number) {
        StringBuilder builder = new StringBuilder();

        while (number > 0) {
            builder.append(base62Characters.charAt((int) (number % base62Characters.length())));
            number /= base62Characters.length();
        }

        return builder.toString();
    }
}
