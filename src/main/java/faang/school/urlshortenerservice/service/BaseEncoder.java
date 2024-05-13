package faang.school.urlshortenerservice.service;

public interface BaseEncoder {

    String encode(long number);

    long decode(String number);

}