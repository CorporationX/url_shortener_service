package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UniqueSequenceIdRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final UniqueSequenceIdRepository uniqueSequenceIdRepository;
    private final HashRepository hashRepository;
    @Value(value = "${hash.range:1000}")
    private int maxRange;

    @Transactional
    @Async("threadPoolTaskExecutor")
    public List<Hash> generateBatch(final int maxRange) {
        List<Long> range = uniqueSequenceIdRepository.getNextRange(maxRange);
        List<Hash> hashes = range.stream()
                .map(this::encoded)
                .map(Hash::new)
                .toList();

        return hashRepository.saveAllAndFlush(hashes);
    }

    public String encoded(Long number) {
        return Base64.getUrlEncoder()
                .encodeToString(String.valueOf(number).getBytes());
    }
}
