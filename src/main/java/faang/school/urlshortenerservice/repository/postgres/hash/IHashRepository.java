package faang.school.urlshortenerservice.repository.postgres.hash;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface IHashRepository {
    void saveBatch(List<Hash> hashes);
}
