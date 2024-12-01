package faang.school.urlshortenerservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class HashProperties {
    @Value("${hash.cache.capacity}")
    private Integer capacity;

    @Value("${hash.cache.low-size-percentage}")
    private Integer lowSizePercentage;

    @Value("${hash.batch_size.get_batch}")
    private Integer batchSizeForGetHashes;

    @Value("${hash.batch_size.generation_batch}")
    private Integer batchSizeForGenerationHashes;

    @Value("${hash.retryable.max_attempts}")
    private String maxAttempts;

    @Value("${hash.retryable.delay}")
    private String delay;

    public double getLowCacheThreshold() {
        return getCapacity() * getLowSizePercentage() / 100.0;
    }
}
