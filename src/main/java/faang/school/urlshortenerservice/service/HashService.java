package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashService {
    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public List<String> getHashes(int amount) {
        List<Hash> hashes = hashRepository.getHashBatch(amount);
        if(hashes.size() < amount) {
            hashGenerator.generateBatch();
            hashes.addAll(hashRepository.getHashBatch(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }
}
