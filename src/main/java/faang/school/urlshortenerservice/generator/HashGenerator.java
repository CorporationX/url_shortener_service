package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.range}")
    private int range;

    @Async("customThreadPoolForHashGenerator")
    @Transactional
    public void generateBatch() {
        List<Long> numbers = hashRepository.getUniqueNumbers(range);
        List<String> hashesEncoder = base62Encoder.encode(numbers);

        List<Hash> hashes = new ArrayList<>();
        hashesEncoder.forEach(hashEncoder -> {
            Hash hash = new Hash();
            hash.setHash(hashEncoder);
            hashes.add(hash);
        });

        hashRepository.saveAll(hashes);
     }

}
