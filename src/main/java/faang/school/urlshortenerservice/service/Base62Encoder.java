package faang.school.urlshortenerservice.service;

import java.util.List;

public interface Base62Encoder {

    String encode(long number);

    List<String> encode(List<Long> numbers);

}