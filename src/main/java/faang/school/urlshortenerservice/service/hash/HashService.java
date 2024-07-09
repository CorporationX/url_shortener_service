package faang.school.urlshortenerservice.service.hash;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface HashService {
    @Transactional(readOnly = true)
    List<Long> getUniqueNumbers(int n);

    @Transactional
    void saveHashes(List<String> hashes);

    @Transactional(readOnly = true)
    List<String> getHashBatch();
}
