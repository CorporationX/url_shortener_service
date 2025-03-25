package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;

public interface HashCustomRepository {

    void hashBatchSave(List<Hash> hashes);
}
