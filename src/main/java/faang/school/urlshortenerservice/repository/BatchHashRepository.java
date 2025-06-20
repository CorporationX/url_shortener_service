package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface BatchHashRepository {

    void saveAllHashes(List<String> hashes);
}
