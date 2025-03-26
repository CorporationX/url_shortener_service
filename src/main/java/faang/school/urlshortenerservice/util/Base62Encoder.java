package faang.school.urlshortenerservice.util;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final Base62 base62 = Base62.createInstance();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .collect(Collectors.toList());
    }

    private String encodeToBase62(Long number) {
        byte[] encoded = base62.encode(BigInteger.valueOf(number).toByteArray());
        return new String(encoded);
    }
}