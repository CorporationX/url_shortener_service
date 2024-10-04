package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BaseEncoder {
    @Value("${encoder.base.alphabet}")
    private String alphabet;

    public String encode(Long uniqueNumber) {
        int alphabetLength = alphabet.length();
        StringBuilder hash = new StringBuilder();
        while (uniqueNumber != 0) {
            hash.append(alphabet.charAt((int) (uniqueNumber % alphabetLength)));
            uniqueNumber /= alphabetLength;
        }
        return hash.toString();
    }
}
