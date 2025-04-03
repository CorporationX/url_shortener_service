package faang.school.urlshortenerservice.service.hash.api;

import java.util.List;

public interface HashGenerator {
    List<String> getHashes(int amount);

    void generateHashes();

    boolean isMinimumThresholdExceeded();
}
