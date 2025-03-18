package faang.school.urlshortenerservice.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepositoryJDBC {

    void save(List<String> hashes);

    List<String> getHashBatch();
}
