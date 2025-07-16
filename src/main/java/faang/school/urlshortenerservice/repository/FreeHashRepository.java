package faang.school.urlshortenerservice.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeHashRepository {
    List<String> fetchFreeHashes(int count);
    default String fetchFreeHash() {
        List<String> list = fetchFreeHashes(1);
        if (list.isEmpty()) {
            throw new IllegalStateException("No free hashes available");
        }
        return list.get(0);
    }
}