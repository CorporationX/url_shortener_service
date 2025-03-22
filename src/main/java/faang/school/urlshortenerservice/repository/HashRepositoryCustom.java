package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashRepositoryCustom {

    void saveAll(List<Hash> hashes);

    void saveHashBatch(List<Object[]> hashes);
}