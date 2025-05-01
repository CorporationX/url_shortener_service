package faang.school.urlshortenerservice.encoder;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private final int base = 62;

    public List<String> encode(List<Long> numbers) {
        List<String> hashes = new ArrayList<>();

        for (Long number : numbers) {
            if (number < base) {
                hashes.add(Character.toString(ALPHABET.charAt(number.intValue())));
                continue;
            }

            long value = number;
            StringBuffer stringBuffer = new StringBuffer();

            while (value != 0) {
                int remind = (int) (value % base);
                value = (value - remind) / base;
                stringBuffer.append(ALPHABET.charAt(remind));
            }
            hashes.add(stringBuffer.toString());
        }
        return hashes;
    }
}
