package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashRepositoryCustom {

    <S extends Hash> List<S> saveAll(Iterable<S> hashes);
}
