package faang.school.urlshortenerservice.generator;

import org.springframework.stereotype.Component;
import io.seruco.encoding.base62.Base62;

import java.nio.ByteBuffer;
import java.util.List;

@Component
public class Base62Encoder {
    private final Base62 base62 = Base62.createInstance();
    private final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::toBase62)
                .toList();
    }

    private String toBase62(Long number) {
        byte[] bytes = buffer.clear()
                .putLong(number)
                .array();
        String hash = new String(base62.encode(bytes));
        return hash.replaceFirst("^0+", "");
    }
}
