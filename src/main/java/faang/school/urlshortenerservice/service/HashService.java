package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashService {

    private final HashRepository hashRepository;
    private final HashCache hashCache;

    public String getHash() {
        return hashCache.getHash();
    }

    public void batchSave(List<String> hashes) {
        hashRepository.batchSave(hashes);
    }
}
