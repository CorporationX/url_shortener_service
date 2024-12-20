package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface CustomHashRepository {
    void saveAllBatched(List<Hash> hashes);
}
