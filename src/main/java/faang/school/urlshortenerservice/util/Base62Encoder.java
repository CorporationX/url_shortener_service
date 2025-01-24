package faang.school.urlshortenerservice.util;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;

@Component
public class Base62Encoder {
    private static final int CUT_HEAD = 3;


    private final Base62 base62 = Base62.createInstance();
    private final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);


    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeOne)
                .toList();
    }


    private String encodeOne(Long number) {
        byte[] bytes = buffer.clear().putLong(number).array();
        String raw = new String(base62.encode(bytes));
        return raw.substring(CUT_HEAD);
    }
}