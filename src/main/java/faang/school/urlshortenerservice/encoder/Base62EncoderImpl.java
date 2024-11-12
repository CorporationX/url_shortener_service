package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class Base62EncoderImpl implements Base62Encoder {

    @Value("${spring.base62.alphabet}")
    private static String alphabet;

    @Value("${spring.base62.base}")
    private static int base;

    public static Hash encodeSingle(long num) {
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % base);
            sb.append(alphabet.charAt(remainder));
            num /= base;
        }
        return Hash.builder()
                .hash(String.valueOf(sb))
                .build();
    }

    public static List<Hash> encode(List<Long> numbers) {
        List<String> encodedList = new ArrayList<>();
        for (Long number : numbers) {
            encodedList.add(encodeSingle(number));
        }
        return encodedList;
    }
}
