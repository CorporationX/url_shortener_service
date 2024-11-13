package faang.school.urlshortenerservice.util;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Encoder<Long, String> {
    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .collect(Collectors.toList());
    }

    public abstract String encode(Long number);
}
