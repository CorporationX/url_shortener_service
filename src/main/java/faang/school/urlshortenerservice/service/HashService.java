package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashService {
    void saveHashes ( List<Hash> hashes );

    List<String> getHashes(long amount);
}
