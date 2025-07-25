package faang.school.urlshortenerservice.hash.encoder;

import faang.school.urlshortenerservice.config.hash.HashProperties;
import faang.school.urlshortenerservice.hash.Base62Encoder;
import io.seruco.encoding.base62.Base62;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {
    private final Base62 base62;
    private final ByteBuffer byteBuffer;
    private final HashProperties hashProperties;

    @Override
    public List<String> encodeBatch(List<Long> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    private String encode(Long number) {
        byte[] bytes = base62.encode(byteBuffer.clear().putLong(number).array());
        String hash = new String(bytes, StandardCharsets.UTF_8);
        if (hash.length() > hashProperties.maxLength()) {
            return hash.substring(hashProperties.maxLength());
        }

        return hash;
    }
}
