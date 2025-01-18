package faang.school.urlshortenerservice.service.base62encoder;

import java.util.List;

public interface Base62Encoder {

    String encode(Long number);

    List<String> encodeListNumbers(List<Long> numbers);
}
