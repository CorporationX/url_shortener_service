package faang.school.urlshortenerservice.hash.encoder;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface Base62 {
    String encode(long number);

    long decode(String number);

    List<String> encode(List<Long> numbers);
}
