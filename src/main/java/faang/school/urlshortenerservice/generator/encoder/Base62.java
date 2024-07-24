package faang.school.urlshortenerservice.generator.encoder;

import java.util.List;

public interface Base62 {
    String encode(Long number);

    List<String> encodeCollection(List<Long> numbers);
}
