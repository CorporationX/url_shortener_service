package faang.school.urlshortenerservice.encoder;


import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface Base62Encoder {

    Hash encodeSingle(long number);

    List<Hash> encode(List<Long> numbers);
}
