package faang.school.urlshortenerservice.util.api;

import java.util.List;

public interface Base62Encoder {
    String[] encodeNumbers(List<Long> numbers);
}
