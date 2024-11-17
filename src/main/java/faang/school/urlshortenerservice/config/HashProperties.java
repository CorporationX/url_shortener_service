package faang.school.urlshortenerservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "hash")
public class HashProperties {
    @Value("${hash.cache.capacity}")
    private Integer capacity;

    @Value("${hash.cache.low-size-percentage}")
    private Integer lowSizePercentage;

    @Value("${hash.batch_size.get_batch}")
    private Integer batchSizeForGetHashes;

    @Value("${hash.batch_size.save_batch}")
    private Integer batchSizeForSaveHashes;

    @Value("${hash.batch_size.generation_batch}")
    private Integer batchSizeForGenerationHashes;
}
