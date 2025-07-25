package faang.school.urlshortenerservice.config.redis;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReferenceArray;

@ConfigurationProperties(prefix = "hash.batch")
@Getter
@Setter
@Component
public class AtomicReferenceConfig {
    private int size;

    @Bean
    public AtomicReferenceArray<Hash> hashAtomicReference() {
        return new AtomicReferenceArray<>(size);
    }
}
