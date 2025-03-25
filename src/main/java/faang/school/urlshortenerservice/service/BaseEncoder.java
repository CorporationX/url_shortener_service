package faang.school.urlshortenerservice.service;

import java.util.List;

public interface BaseEncoder {

    List<String> encode(List<Long> numbers);
}