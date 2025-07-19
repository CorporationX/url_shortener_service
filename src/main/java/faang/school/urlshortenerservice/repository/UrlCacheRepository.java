package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.dto.UrlDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlCacheRepository {

    private static final String U_KEY = "URL_KEY:";
    private static final String H_KEY = "HASH_KEY:";

    private final StringRedisTemplate stringRedisTemplate;

    public Optional<String> findByHash(String hash) {
        String key = H_KEY + hash;
        log.debug("findByHash >> key: {}", key);
        Optional<String> result = Optional.ofNullable(mapOps().get(key));
        log.debug("findByHash hash:{}; url: {}", hash, result);
        return result;
    }

    public void addUrl(UrlDto urlDto) {
        log.debug("addUrl hash:{}; url: {}", urlDto.getHash(), urlDto.getUrl());
        log.debug("set >> key: {}, value : {}", H_KEY + urlDto.getHash(), urlDto.getUrl());
        log.debug("set >> key: {}, value : {}", U_KEY + urlDto.getUrl(), urlDto.getHash());
        mapOps().setIfAbsent(H_KEY + urlDto.getHash(), urlDto.getUrl());
        mapOps().setIfAbsent(U_KEY + urlDto.getUrl(), urlDto.getHash());
    }

    public Optional<String> findByUrl(String url) {
        String key = U_KEY + url;
        log.debug("findByUrl >> key: {}", key);
        Optional<String> result = Optional.ofNullable(mapOps().get(key));
        log.debug("findByUrl url:{}; url: {}", url, result);
        return result;
    }

    public void deleteUrls(List<String> urls) {
        log.debug("delete >> urls: {}", urls);
        List<String> workingUrls = urls.stream()
            .map(url -> U_KEY + url)
            .toList();
        stringRedisTemplate.delete(workingUrls);
    }

    public void deleteHashes(List<String> hashes) {
        log.debug("delete >> hashes: {}", hashes);
        List<String> workingHashes = hashes.stream()
            .map(hash -> H_KEY + hash)
            .toList();
        stringRedisTemplate.delete(workingHashes);
    }

    // Получение набора операций для мапы
    private ValueOperations<String, String> mapOps() {
        return stringRedisTemplate.opsForValue(); // Операции над мапой
    }
}
