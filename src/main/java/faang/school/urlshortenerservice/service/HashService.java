package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.generator.HashGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {
    private final HashGenerator hashGenerator;
    private final HashCache hashCache;

    public String getNextHash() {
        return hashCache.poll();
    }

    @Async("hashGeneratorExecutor")
    public void generateMoreHashes() {
        List<String> hashes = hashGenerator.generateBatch();
        hashCache.addAll(hashes);
    }
}