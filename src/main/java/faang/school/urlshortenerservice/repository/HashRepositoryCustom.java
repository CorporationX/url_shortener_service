package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashRepositoryCustom {
    void saveAllBatched(List<Hash> entities, int batchSize);
}
