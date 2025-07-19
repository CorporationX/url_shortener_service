package faang.school.urlshortenerservice.utils;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
    // TODO: base62
    public String encode(long value) {
        return String.valueOf(value);
    }
}
