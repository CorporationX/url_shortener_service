package faang.school.urlshortenerservice.service.imp;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.service.HashGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class HashGeneratorServiceImp implements HashGeneratorService {

    private final HashRepository hashRepository;

    @Override
    public List<String> generateHashes(int count) {
        List<String> newHashes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            newHashes.add(generateRandomHash());
        }
        return newHashes;
    }

    @Override
    public void saveGeneratedHashesToDatabase(List<String> hashes) {
        for (String hash : hashes) {
            Hash newHash = new Hash();
            newHash.setHash(hash);
            hashRepository.save(newHash);
        }
    }

    private String generateRandomHash() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
}
