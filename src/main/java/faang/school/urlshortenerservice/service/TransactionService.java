package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.encoder.Base62Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    @Transactional
    public List<String> saveHashBatch(int batchSize) {
        List<Long> nums = hashRepository.getUniqueNumbers(batchSize);
        List<String> encodedNums = base62Encoder.encodeBatch(nums);
        List<Hash> hashBatch = encodedNums.stream()
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashBatch);
        return encodedNums;
    }

    @Transactional
    public List<String> getHashBatch(int batchSize) {
        return hashRepository.removeAndGetHashBatch(batchSize);
    }
}
