package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;

import java.util.List;

public interface Base62Encoder {
    public List<Hash> encode(List<Long> numbers);
}