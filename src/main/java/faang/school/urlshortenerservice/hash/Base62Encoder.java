package faang.school.urlshortenerservice.hash;

import java.util.List;

public interface Base62Encoder {
    List<String> encodeBatch(List<Long> numbers);
}
