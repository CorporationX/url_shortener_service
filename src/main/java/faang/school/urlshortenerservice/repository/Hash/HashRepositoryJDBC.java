package faang.school.urlshortenerservice.repository.Hash;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepositoryJDBC {

    void save(List<String> hashes);

    List<String> getHashBatch();
}
