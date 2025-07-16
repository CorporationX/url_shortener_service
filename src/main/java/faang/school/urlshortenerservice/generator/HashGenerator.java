package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    @Value("${hash.range:1000}")
    int range;

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void generateHashRange() {
        List<Long> nextRange = hashRepository.getNextRange(range);
        List<Hash> hashes = nextRange.stream()
                .map(this::applyBase62Encode)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    private String applyBase62Encode(Long number) {
        String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.append(base62.charAt((int) (number % 62)));
            number /= 62;
        }
        return encoded.reverse().toString();
    }
}
