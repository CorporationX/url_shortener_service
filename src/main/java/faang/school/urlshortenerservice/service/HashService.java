package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.properties.HashProperties;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final HashProperties hashProperties;

    @Transactional
    public void saveHashes(List<Hash> hashes) {
        hashRepository.saveHashes(hashes);
    }

    public List<Long> getUniqueNumbers(long n) {
        return hashRepository.getUniqueNumbers(n);
    }
}