package faang.school.urlshortenerservice.encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class Base64FixLengthEncoder implements HashEncoder {

    private final String base64Chars = "_abcdefghijklmnopqrstuvwxyz0123456789-ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final int codeLength = base64Chars.length();
    private final int hashLength;

    public Base64FixLengthEncoder(@Value("${configs.encoder.hash-length}") int hashLength) {
        this.hashLength = hashLength;
    }

    @Override
    public Set<String> encode(List<Long> uniqueNumbers) {
        return uniqueNumbers.stream()
                .map(this::encodeToFixLength)
                .collect(Collectors.toSet());
    }

    String encodeToFixLength(Long uniqueNumber) {
        StringBuilder encoded = new StringBuilder();
        while (uniqueNumber > 0) {
            int index = (int) (uniqueNumber % codeLength);
            encoded.append(base64Chars.charAt(index));
            uniqueNumber /= codeLength;
        }
        while (encoded.length() < hashLength) {
            encoded.append(base64Chars.charAt(0));
        }
        return encoded.toString();
    }

}
