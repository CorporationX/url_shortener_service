package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class HashGenerator {

    public String generateHash() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
