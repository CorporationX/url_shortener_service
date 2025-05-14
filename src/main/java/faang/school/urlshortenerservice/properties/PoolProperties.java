package faang.school.urlshortenerservice.properties;

public interface PoolProperties {
    int size();
    int shutdownTimeoutSetting();
    boolean isWaitShutdown();
    String threadPrefix();
}
