package faang.school.urlshortenerservice.service;

import java.util.List;

public interface BaseEncoder {

    public List<String> encode(List<Long> numbers);
    String encode(Long number);
}
