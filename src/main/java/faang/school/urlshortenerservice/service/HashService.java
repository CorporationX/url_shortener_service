package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HashService {

    List<Hash> saveAllHashes(List<Hash> hashes);

    void deleteHashFromDataBase();
}
