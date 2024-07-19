package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;

@Component
public interface HashGenerator {
    void generateBatch(int n);
}
