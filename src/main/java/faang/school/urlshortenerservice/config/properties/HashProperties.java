package faang.school.urlshortenerservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "hash")
public record HashProperties(Integer batchsize){}
