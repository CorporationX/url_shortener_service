package faang.school.urlshortenerservice.encoder;

import java.util.List;
import java.util.Set;

public interface HashEncoder {

    Set<String> encode(List<Long> input);
}
