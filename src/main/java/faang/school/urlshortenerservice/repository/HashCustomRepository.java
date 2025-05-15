package faang.school.urlshortenerservice.repository;

import java.util.List;

public interface HashCustomRepository {
    void saveHashesByBatch(List<String> hashes);

}
