package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService{

    private final HashRepository hashRepository;
    private final HashGenerator hashGenerator;

    @Transactional
    public void saveHashes (List<Hash> hashes) {
        hashRepository.saveAll(hashGenerator.generateBatch());
    }

    @Transactional
    public List<String> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if(hashes.size() < amount) {
            hashGenerator.generateBatch();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes.stream().map(Hash::getHash).toList();
    }
}
