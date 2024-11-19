package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.model.hash.Hash;

import java.util.List;

public interface CustomHashRepository {
    void saveAllBatched(List<Hash> hashes);
}
