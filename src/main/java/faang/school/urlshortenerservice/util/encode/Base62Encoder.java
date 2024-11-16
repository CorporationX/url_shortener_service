package faang.school.urlshortenerservice.util.encode;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Component
public class Base62Encoder {
    private final Base62 base62 = Base62.createInstance();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .toList();
    }

    private String encodeToBase62(Long number) {
        byte[] bytes = compactLongToBytes(number);
        return new String(base62.encode(bytes));
    }

    private byte[] compactLongToBytes(long number) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (number != 0) {
            bos.write((byte) (number & 0xFF));
            number >>>= 8;
        }
        return bos.toByteArray();
    }
}
