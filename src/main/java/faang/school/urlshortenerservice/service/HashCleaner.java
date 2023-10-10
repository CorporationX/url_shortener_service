package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashJpaRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashCleaner {
    private final HashJpaRepository hashJdbcRepository;
    private final UrlRepository urlRepository;

    @Transactional
    public void hashClear() {
        List<Hash> hashes = new ArrayList<>();
        List<String> list = urlRepository.deleteExpiredHashes();
        for (String hash : list) {
            hashes.add(new Hash(hash));
        }
        hashJdbcRepository.saveBatch(hashes);
        log.info("Saved: {}", hashes);
    }
}
