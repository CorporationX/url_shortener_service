package faang.school.urlshortenerservice.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash-generator-setting")
public record HashGeneratorProperties(
        int countReturningUniqueNumbers,
        int batchSizeProcessingHashes,
        int availableHashesOnRepository
) {}
