package faang.school.urlshortenerservice.HashGenerator;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
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
    @Value("${hash.batch-size}")
    private int batch;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Async("hashGeneratorThreadPool")
    public void generateHash() {
        List<Long> numbers =  hashRepository.getUniqueNumbers(batch);
        List<Hash> hashes = base62Encoder.encode(numbers).stream().map(Hash::new).toList();
        hashRepository.saveAll(hashes);
    }
}
