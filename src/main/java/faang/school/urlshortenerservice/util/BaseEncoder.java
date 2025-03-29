package faang.school.urlshortenerservice.util;

public interface BaseEncoder {
    String encode(long number);
    long decode(String number);
}
