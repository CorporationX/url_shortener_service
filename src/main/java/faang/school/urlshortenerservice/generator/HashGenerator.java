package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;

    @Value("${hash.range:1000}")
    private int range;

    @Transactional
    @Scheduled(cron = "0 0 * * * ?")
    public void generateHashRange() {
        List<Long> nextRange = hashRepository.getUniqueNumbers(range);
        List<String> hashes = nextRange.stream()
                .map(this::applyBase62Encode)
                .toList();
        hashRepository.save(hashes);
    }

    private String applyBase62Encode(Long number) {
        final String base62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (number == 0) return "0";
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            encoded.append(base62.charAt((int) (number % 62)));
            number /= 62;
        }
        return encoded.reverse().toString();
    }
}
