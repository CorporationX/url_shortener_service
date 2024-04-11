package faang.school.urlshortenerservice.hash.generator;

import org.springframework.scheduling.annotation.Async;

public interface HashGenerator {

    @Async
    void generateHash();

}
