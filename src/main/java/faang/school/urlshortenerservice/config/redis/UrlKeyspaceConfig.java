package faang.school.urlshortenerservice.config.redis;

import faang.school.urlshortenerservice.model.UrlCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration;

import java.util.Collections;

@Slf4j
public class UrlKeyspaceConfig extends KeyspaceConfiguration {

    @Value("${spring.data.redis.time-to-live:60000}")
    private Long timeToLive;

    @Override
    protected Iterable<KeyspaceSettings> initialConfiguration() {
        KeyspaceSettings keyspaceSettings = new KeyspaceSettings(UrlCache.class, "url");
        keyspaceSettings.setTimeToLive(timeToLive);
        return Collections.singleton(keyspaceSettings);
    }
}