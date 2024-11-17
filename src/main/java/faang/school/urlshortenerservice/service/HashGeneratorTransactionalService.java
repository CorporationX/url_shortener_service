package faang.school.urlshortenerservice.service;

import java.util.List;

public interface HashGeneratorTransactionalService {
    List<String> getHashes(long requiredAmount, int batchSize);
    List<String> generateInitBatch(int batchSize);
}
