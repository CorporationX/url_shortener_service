package faang.school.urlshortenerservice.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MaliciousHostValidator {

    @Value("classpath:blacklist.txt")
    private Resource blacklistFile;

    private final List<String> forbiddenWords = new ArrayList<>();

    @PostConstruct
    void init() {
        try (var reader = new BufferedReader(new InputStreamReader(blacklistFile.getInputStream()))) {
            reader.lines()
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(forbiddenWords::add);
            log.info("Loaded {} forbidden words", forbiddenWords.size());
        } catch (Exception e) {
            log.warn("Failed to read blacklist file", e);
        }
    }

    public boolean isHostSafe(String url) {
        try {
            var host = new URL(url).getHost().toLowerCase();
            return forbiddenWords.stream().noneMatch(host::contains);
        } catch (Exception e) {
            log.warn("Error parsing URL", e);
            return false;
        }
    }
}
