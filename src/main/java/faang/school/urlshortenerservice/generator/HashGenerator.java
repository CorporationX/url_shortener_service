package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashGenerator {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    @Value("${generator.hash.count}")
    private long countUniqueNumbers;

    @Async("asyncExecutor")
    @Transactional
    public void generateBatch(){
        List<Long> uniqueNumbers = hashRepository.getUniqueNumbers(countUniqueNumbers);
        List<Hash> hashes = base62Encoder.encode(uniqueNumbers).stream().map(Hash::new).toList();
        hashRepository.saveAll(hashes);
    }
}