package faang.school.urlshortenerservice.util;

import java.util.List;

public interface Base62Encoder {
    List<String> encodeBatch(List<Long> numbers);
    String encode(Long number);
}
