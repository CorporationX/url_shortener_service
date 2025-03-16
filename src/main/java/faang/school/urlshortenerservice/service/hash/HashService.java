package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;

public interface HashService {

    List<Long> getNewNumbers(Long n);

    Long getHashesCount();

    List<Hash> getBatchHashesAndDelete(int size);

    void saveHashesBatch(List<Hash> hashes);
}
