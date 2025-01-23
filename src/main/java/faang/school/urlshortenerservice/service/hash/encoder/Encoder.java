package faang.school.urlshortenerservice.service.hash.encoder;

import java.util.List;

public interface Encoder {
    List<String> encode(List<Long> values, int hashLength);
}
