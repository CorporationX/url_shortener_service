package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;

public interface HashService {

    List<Hash> generateHashes(int size);

    void saveHashes(List<Hash> hashes);

    List<Hash> readFreeHashes(int quantity);

}
