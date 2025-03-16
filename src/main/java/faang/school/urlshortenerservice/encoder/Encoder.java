package faang.school.urlshortenerservice.encoder;

import java.util.List;

public interface Encoder {

    String encode(Long sequenceNumber);

    List<String> encode (List<Long> sequenceNumbers);
}
