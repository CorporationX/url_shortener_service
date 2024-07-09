package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    private final HashRepository hashRepository;

    @Value("batch-size.hash")
    private int batchSize;

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUniqueNumbers(int n) {
        return hashRepository.getUniqueNumbers(n);
    }

    @Override
    @Transactional
    public void saveHashes(List<String> hashes) {
        for (int i = 0; i < hashes.size(); i += batchSize) {
            List<Hash> batch = hashes.subList(i, Math.min(hashes.size(), i + batchSize)).stream()
                    .map(Hash::new)
                    .collect(Collectors.toList());
            hashRepository.saveAll(batch);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getHashBatch() {
        return hashRepository.getHashBatch(batchSize);
    }
}