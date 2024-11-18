package faang.school.urlshortenerservice.repository.hash;

import java.util.List;

public interface FreeHashRepository {

    void saveHashes(List<String> hashes);

    List<String> findAndDeleteFreeHashes(int amount);
}
