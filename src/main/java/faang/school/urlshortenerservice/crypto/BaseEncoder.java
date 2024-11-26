package faang.school.urlshortenerservice.crypto;

import java.util.List;

public interface BaseEncoder {

    CryptoType getEncodeType();

    List<String> encode(List<Long> numbers);
}