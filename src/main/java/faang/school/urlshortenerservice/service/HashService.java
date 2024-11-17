package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class HashService {
    private final HashRepository hashRepository;

    @Value("${app.unique-count}")
    private Integer count;

    @Value("${app.hash-batch-size}")
    private Integer limit;

    @Transactional
    public List<Hash> save(List<String> hashes) {
        List<Hash> urlHashes = hashes.stream().map(Hash::new).toList();
        return hashRepository.saveAll(urlHashes);
    }

    public List<Long> getUniqueNumbers() {
        return hashRepository.getUniqueNumbers(count);
    }

    @Transactional
    public List<String> getAndDeleteHashBatch() {
        return hashRepository.getAndDeleteHashBatch(limit);
    }
}
