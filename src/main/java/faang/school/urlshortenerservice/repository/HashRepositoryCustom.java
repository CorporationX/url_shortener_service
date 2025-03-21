package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashRepositoryCustom {

    void save(List<Hash> hashes);
}