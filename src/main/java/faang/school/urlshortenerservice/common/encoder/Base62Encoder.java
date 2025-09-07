package faang.school.urlshortenerservice.common.encoder;

import java.util.List;

public interface Base62Encoder {
    List<String> encode(List<Long> numbers);
}