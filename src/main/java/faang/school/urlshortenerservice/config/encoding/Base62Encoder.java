package faang.school.urlshortenerservice.config.encoding;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(String::valueOf)
                .toList();
    }
}
