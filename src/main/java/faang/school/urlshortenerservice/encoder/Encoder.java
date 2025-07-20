package faang.school.urlshortenerservice.encoder;

import java.util.List;

public interface Encoder {
    List<String> encode(List<Long> numbers);
}