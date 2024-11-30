package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import io.seruco.encoding.base62.Base62;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {
    private static final int SUBSTRING_INDEX = 3;
    private final Base62 base62 = Base62.createInstance();
    private final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    public List<Hash> encodeList(List<Long> nums) {
        return nums.stream()
                .map(this::encode)
                .map(Hash::new)
                .toList();
    }

    private String encode(Long number) {
        buffer.clear();
        byte[] bytes = buffer.putLong(number).array();
        byte[] encodedBytes = base62.encode(bytes);
        return new String(encodedBytes).substring(SUBSTRING_INDEX);
    }
}
