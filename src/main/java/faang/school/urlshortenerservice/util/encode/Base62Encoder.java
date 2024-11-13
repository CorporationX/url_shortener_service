package faang.school.urlshortenerservice.util.encode;

import faang.school.urlshortenerservice.annotation.logging.LogExecution;
import faang.school.urlshortenerservice.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
public class Base62Encoder {
    @Value("${app.encoder.base_62.characters}")
    private String characters;

    @Value("${app.encoder.base_62.hash_length}")
    private int hashLength;

    @Value("${app.encoder.base_62.base}")
    private int base;

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .toList();
    }

    public String encodeToBase62(long number) {
        StringBuilder encoded = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % base);
            encoded.insert(0, characters.charAt(remainder));
            number /= base;
        }
        lengthCheck(encoded);
        return encoded.length() > hashLength ? encoded.substring(0, hashLength) : encoded.toString();
    }

    @LogExecution
    private void lengthCheck(StringBuilder encoded) {
        if (encoded.length() > 6)
            throw new ApiException("Hash: %s length more than limit: %s", INTERNAL_SERVER_ERROR, encoded, hashLength);
    }
}
