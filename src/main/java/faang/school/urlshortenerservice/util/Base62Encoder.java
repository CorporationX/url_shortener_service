package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;
import org.unbrokendome.base62.Base62;

import java.util.List;

@Component
public class Base62Encoder {
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(long number) {
        String encoded = Base62.encode(number);
        return encoded.substring(5, 11);
    }
}
