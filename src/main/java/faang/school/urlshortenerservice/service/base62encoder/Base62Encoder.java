package faang.school.urlshortenerservice.service.base62encoder;

import java.util.List;
import java.util.Set;

public interface Base62Encoder {

    String encode(Long number);

    List<String> encodeListNumbers(Set<Long> numbers);
}
