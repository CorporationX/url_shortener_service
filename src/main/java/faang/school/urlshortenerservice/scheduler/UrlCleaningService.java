package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UrlCleaningService {

    private final UrlRepository urlRepository;
    private final HashRepository hashRepository;

    @Value("${app.cleaner.batch-size}")
    private int batchSize;

    @Value("${app.cleaner.age-days}")
    private long ageDays;

    @Transactional
    public List<String> cleanOneBatch() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(ageDays);
        List<String> hashes = urlRepository.deleteOldHashes(cutoff, batchSize);
        if (!hashes.isEmpty()) {
            hashRepository.saveAll(
                    hashes.stream()
                            .map(Hash::new)
                            .toList()
            );
        }
        return hashes;
    }
}
