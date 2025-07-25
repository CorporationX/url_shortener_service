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
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService{
    private final UrlRepository urlRepository;
    private final UrlHashCacheService urlHashCacheService;
    private final HashCache hashCache;
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
    public String findUrlByHash(String hash) {
        urlHashCacheService.getUrlByHash(hash);
        Optional<Url> urlFromDb = urlRepository.findById(hash);
        if(urlFromDb.isPresent()) {
            urlHashCacheService.cacheHashUrl(hash, urlFromDb.get().getUrl());
            return urlFromDb.get().getUrl();
        } else{
            throw new NoSuchElementException();
        }
    }
    @Override
    public Url createUrl (Url url){
        RList<Hash> hashes = hashCache.saveToRedisHash();
        Hash hashRList = hashCache.randomIndex(hashes);
        url.setHash(hashRList.getHash());
        urlHashCacheService.cacheHashUrl(hashRList.getHash(), url.getUrl());
        hashes.remove(hashRList);
        return urlRepository.save(url);
    }
}
