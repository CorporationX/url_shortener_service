package faang.school.urlshortenerservice.util;

import java.util.List;

public interface Base62Encoder {

    List<String> encode(List<Long> numbers);
}
