package faang.school.urlshortenerservice.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HashRepositoryCustom {
    void saveBatch(List<String> hashes);
}
