package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.Hash;

import java.util.List;

public interface Base62Encoder {
    List<Hash> encode(List<Integer> numbers);
}
