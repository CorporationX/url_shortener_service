package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface HashService {
    public List<Long> getUniqueNumbers(long n);

    public void saveHashes(List<Hash> hashes);
}
