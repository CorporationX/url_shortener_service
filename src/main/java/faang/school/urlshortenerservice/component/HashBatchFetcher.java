package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HashBatchFetcher {

    private final HashRepository hashRepository;

    @Transactional
    public List<String> fetchHashes(int batchSize) {
        return hashRepository.getHashBatch(batchSize);
    }
}
