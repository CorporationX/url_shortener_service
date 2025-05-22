package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.enity.FreeHash;

import java.util.List;

public interface HashService {

    List<String> getHashes();

    void saveAll(List<FreeHash> hashes);
}
