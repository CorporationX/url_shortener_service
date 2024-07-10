package faang.school.urlshortenerservice.encoder;


import faang.school.urlshortenerservice.model.Hash;

import java.util.List;

public interface Encoder<T> {
    List<Hash> encode(List<T> keys);
}