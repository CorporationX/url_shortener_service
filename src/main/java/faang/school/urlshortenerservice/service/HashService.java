package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.CustomHashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {

    private final CustomHashRepository customHashRepository;

    public String getHash() {
        List<String> hashes = customHashRepository.getHashBatch(1);
        return hashes.isEmpty() ? null : hashes.get(0);
    }

    public void saveHashes(List<String> hashes) {
        customHashRepository.saveHashesBatch(hashes);
    }
}