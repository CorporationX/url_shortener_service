package faang.school.urlshortenerservice.hash.encoder;

import java.util.List;

public interface Encoder {
    List<String> encode(List<Long> nums);
}
