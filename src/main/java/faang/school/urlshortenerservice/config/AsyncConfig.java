package faang.school.urlshortenerservice.config;

public interface AsyncConfig {
    int getCorePoolSize();
    int getMaxPoolSize();
    int getQueueCapacity();
}
