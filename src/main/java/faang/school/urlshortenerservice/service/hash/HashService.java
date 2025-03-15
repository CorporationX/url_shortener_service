package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;

public interface HashService {

    List<Long> getNewNumbers();

    void saveHashes(List<Hash> hashes);

    List<Hash> getBatchHashesAndDelete();
}
