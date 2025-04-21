package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface HashService {

    void saveHashBatch(List<Hash> hashes);
}
