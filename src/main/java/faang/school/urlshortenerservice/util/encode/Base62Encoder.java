package faang.school.urlshortenerservice.util.encode;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.LongStream;

@Component
public class Base62Encoder {
    private final Base62 base62 = Base62.createInstance();

    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeToBase62)
                .toList();
    }

    private String encodeToBase62(Long number) {
        byte[] bytes = longToBytes(number);
        String hash = new String(base62.encode(bytes));
        return hash.replaceFirst("^0+", "");
    }

    private byte[] longToBytes(long number) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(number);
        return buffer.array();
    }
}
