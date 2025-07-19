package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import io.seruco.encoding.base62.Base62;

import java.nio.ByteBuffer;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {
    private final Base62 base62;
    private final ThreadLocal<ByteBuffer> byteBufferThreadLocal =
            ThreadLocal.withInitial(()-> ByteBuffer.allocate(Long.BYTES));

    @Override
    public List<String> encode(List<Long> numbers) {
        try {
            ByteBuffer byteBuffer = byteBufferThreadLocal.get();
            byteBuffer.clear();
            byte[] bytes;

        }
        finally {

        }
        return List.of();
    }
}
