package faang.school.urlshortenerservice.service;

import java.time.LocalDateTime;
import java.util.List;

public interface CleanupService {
    List<String> deleteExpiredBatch(LocalDateTime expiryDate, int batchSize);
    void returnHashesToPool(List<String> hashes);
}
