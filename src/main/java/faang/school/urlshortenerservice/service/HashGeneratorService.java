package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.BatchRepository;
import faang.school.urlshortenerservice.utils.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorService {

    private final BatchRepository hashRepository;

    @Value("${hash.range:10000}")
    private int maxRange;

    @Transactional
    public List<Hash> getHashes(long amount) {
        List<Hash> hashes = hashRepository.findAndDelete(amount);
        if (hashes.size() < amount) {
            generateAndSaveNewHashes();
            hashes.addAll(hashRepository.findAndDelete(amount - hashes.size()));
        }
        return hashes;
    }

    private void generateAndSaveNewHashes() {
        List<Hash> hashes = generateNewHashes(maxRange);
        hashRepository.saveAll(hashes);
    }

    private List<Hash> generateNewHashes(int maxRange) {
        List<Long> uniqueNumbers = hashRepository.getNextRange(maxRange);
        return uniqueNumbers.stream()
                .map(Base62Encoder::encode)
                .map(Hash::new)
                .toList();
    }
}
