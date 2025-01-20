package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableAsync
public class HashGenerator {

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Value("${hash.generate.size}")
    public Integer size;

    @Async("hashGeneratorThreadPool")
    @Transactional
    public void generateHashes() {
        List<Long> listUniqueNumbers = hashRepository.getUniqueNumbers(size);
        List<String> hashList = base62Encoder.encode(listUniqueNumbers);
        hashRepository.saveAllHashes(hashList);
    }
}
