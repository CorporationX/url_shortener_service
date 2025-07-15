package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.util.Base62Encoder;
import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {
    private final Base62 base62;
    private final ByteBuffer byteBuffer;

    @Value("${hash.max_length}")
    private int hashLength;

    @Override
    public List<String> encodeBatch(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    @Override
    public String encode(Long number) {
        byte[] bytes = base62.encode(byteBuffer.clear().putLong(number).array());
        return new String(bytes, StandardCharsets.UTF_8).substring(hashLength);
    }
}
