package faang.school.urlshortenerservice.util;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
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
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        byte[] bytes = buffer.putLong(number).array();
        return new String(base62.encode(bytes)).substring(3);
    }

    public String generateHash() {
        return encodeToBase62(System.nanoTime());
    }
}