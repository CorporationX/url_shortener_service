package faang.school.urlshortenerservice.service.hash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
@Slf4j
@Qualifier("base62Encoder")
public class Base62Encoder extends Encoder {

    private final String charSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final char paddingChar = '0';
    private final int alphaBetSize = 62;
    private final int hashLength = 6;

    protected String encodeNumber(int number) {
        log.info("Trying to encode number {}", number);
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = number % alphaBetSize;
            number = number / alphaBetSize;
            encoded.insert(0, charSet.charAt(remainder));
        }

        while (encoded.length() < hashLength) {
            encoded.insert(0, paddingChar);
        }

        return encoded.toString();
    }
}
