package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface Encoder {

    Hash encode(long number);

    List<Hash> encodeBatch(List<Long> numbers);
}
