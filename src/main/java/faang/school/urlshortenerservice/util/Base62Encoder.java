package faang.school.urlshortenerservice.util;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class Base62Encoder {
    private static final Base62 base62 = Base62.createInstance();

    public String encodeSingle(Long number) {
        byte[] encoded = base62.encode(BigInteger.valueOf(number).toByteArray());
        return new String(encoded);
    }

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeSingle)
                .collect(Collectors.toList());
    }
}