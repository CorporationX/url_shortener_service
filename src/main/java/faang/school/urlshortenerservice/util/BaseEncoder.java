package faang.school.urlshortenerservice.util;


import java.util.List;

public interface BaseEncoder {
    List<String> encode(List<Long> numbers);

}
