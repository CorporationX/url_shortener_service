package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "app.service.hash",
        havingValue = "default",
        matchIfMissing = true
)
public class HashServiceImpl implements HashService {

    private final HashRepository hashRepository;

    @Value("${hash.threshold}")
    private int threshold;

    @Override
    public List<String> getHashBatch(int quantity) {
        return hashRepository.getHashBatch(quantity);
    }

    @Override
    public void save(List<String> hashes) {
        hashRepository.save(hashes);
    }

    @Override
    public boolean isNeedGenerateHash() {
        return hashRepository.getHashCount() < threshold;
    }
}
