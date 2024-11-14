package faang.school.urlshortenerservice.repository.postgres.hash;

import java.util.List;

public interface IHashRepository {
    void saveBatch(List<String> hashes);
}
