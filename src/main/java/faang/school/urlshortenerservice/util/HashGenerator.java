package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${url-shortener.unique-numbers-from-sequence}")
    private Long amountOfNumbersFromSequence;

    @Value("${url-shortener.short-free-url-amount}")
    private Long amountOfFreeShortUrlsFromDB;

    @Async("taskExecutor")
    public void uploadBatch(Long amountOfNumbersFromSequence) {
        List<Hash> hashes = generateBatch(amountOfNumbersFromSequence);
        hashRepository.saveAll(hashes);
    }

    private List<Hash> generateBatch(Long amountOfNumbersFromSequence) {
        List<Long> numbersToDecode = hashRepository.getUniqueNumbers(amountOfNumbersFromSequence);

        return numbersToDecode.parallelStream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }
}
