package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62 base62;

    @Value("${hash.range:100}")
    private int maxRange;

    @Transactional
    @Async("hashGeneratorTaskExecutor")
    public void generateHashBatch() {
        List<Long> range = hashRepository.getNextRange(maxRange);

        List<Hash> hashes = range.stream()
                .map(base62::encode)
                .map(Hash::new)
                .collect(Collectors.toList());

        hashRepository.saveAll(hashes);
    }
}
