package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.Url;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UrlCacheRepository {

  @Autowired
  RedisTemplate<String, String> redisTemplate;

  public void add(Url url) {
    redisTemplate.opsForValue().set(url.getHash(), url.getUrl());
  }

  public String findUrlByHash(String hash) {
    return redisTemplate.opsForValue().get(hash);
  }

}
