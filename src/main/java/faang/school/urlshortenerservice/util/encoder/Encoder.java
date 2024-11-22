package faang.school.urlshortenerservice.util.encoder;

import java.util.List;

public interface Encoder {
    List<String> encodeBatch(List<Long> sequence);
}
