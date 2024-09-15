package faang.school.urlshortenerservice.encoder;

import java.util.List;

public interface BaseEncoder {

    List<String> batchEncoding(List<Long> numbers);

    String encode(long number);
}
