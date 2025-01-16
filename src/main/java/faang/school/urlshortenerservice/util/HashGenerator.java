package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorExecutor")
    public void uploadBatch(Long amountOfNumbersFromSequence) {
        List<Hash> hashes = generateBatch(amountOfNumbersFromSequence);
        hashRepository.saveAll(hashes);
    }

    private List<Hash> generateBatch(Long amountOfNumbersFromSequence) {
        List<Long> numbersToDecode = hashRepository.getUniqueNumbersFromSequence(amountOfNumbersFromSequence);

        return numbersToDecode.parallelStream()
                .map(base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }
}
