package faang.school.urlshortenerservice.service.encoder;

import java.util.List;

public interface Encoder {
    List<String> encodeList(List<Long> numbers);
}
