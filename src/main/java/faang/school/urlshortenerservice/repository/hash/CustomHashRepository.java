package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface CustomHashRepository {
    void saveAllInBatch(List<Hash> hashes);
}