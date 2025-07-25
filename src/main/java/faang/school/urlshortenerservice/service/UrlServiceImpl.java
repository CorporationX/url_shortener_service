package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService{
    private final UrlRepository urlRepository;
    @Transactional
    @Override
    public void deleteUrlOlderOneYearAndSaveByHash(int limit) {
        List<String> hashes = urlRepository.findExpiredUrlsHashes(limit);
        urlRepository.deleteAllByIdInBatch(hashes);
    }
    @Transactional(readOnly = true)
    @Override
    public int countUrlsOlder(){
        return urlRepository.countOfOldUrl();
    }

    @Override
    public Optional<Url> findUrlByHash(String hash) {
        return urlRepository.findById(hash);
    }
}
