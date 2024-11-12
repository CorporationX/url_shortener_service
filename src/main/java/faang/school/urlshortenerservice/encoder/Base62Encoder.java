package faang.school.urlshortenerservice.encoder;


import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface Base62Encoder {

    List<Hash> encode(List<Long> numbers);
}
