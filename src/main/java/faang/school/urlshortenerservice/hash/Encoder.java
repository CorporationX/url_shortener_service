package faang.school.urlshortenerservice.hash;

import java.util.List;

public interface Encoder {
    List<String> encode(List<Long> numbers);
    String encode(long number);
}
