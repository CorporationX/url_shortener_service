package faang.school.urlshortenerservice.util;

import io.seruco.encoding.base62.Base62;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Пример класса, кодирующего Long -> base62 строку,
 * с использованием библиотеки io.seruco.encoding.base62.
 */
@Component
public class Base62Encoder {

    private final Base62 base62 = Base62.createInstance();
    private final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);


    public List<String> encode(List<Long> numbers) {
        return numbers.stream()
                .map(this::encodeOne)
                .toList();
    }


    private String encodeOne(Long number) {
        byte[] bytes = buffer.clear().putLong(number).array();

        byte[] encodedBytes = base62.encode(bytes);

        return new String(encodedBytes);
    }
}