package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService{
    @Value("${hash.batch.limit}")
    private int limit;

    private final HashRepository hashRepository;
    @Override
    public List<Hash> saveAllHashes(List<Hash> hashes) {
        return hashRepository.saveAll(hashes);
    }

    @Override
    public void deleteHashFromDataBase() {
        hashRepository.getHashBatch(limit);
    }

    @Override
    public List<String> saveAllHash(List<String> stringList) {
        return hashRepository.saveByHashList(stringList);
    }
}
