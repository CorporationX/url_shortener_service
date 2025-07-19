package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.codec.EncodingException;
import org.springframework.stereotype.Component;
import io.seruco.encoding.base62.Base62;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@RequiredArgsConstructor
public class Base62EncoderImpl implements Base62Encoder {
    private final Base62 base62;
    private final ThreadLocal<ByteBuffer> byteBufferThreadLocal =
            ThreadLocal.withInitial(() -> ByteBuffer.allocate(Long.BYTES));

    @Value("${hash.max_length}")
    private int hashLength;

    @Override
    public List<String> encode(List<Long> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            log.debug("Empty List for hash encode");
            return Collections.emptyList();
        }
        ByteBuffer byteBuffer = byteBufferThreadLocal.get();
        List<String> encodedHashes = new ArrayList<>(numbers.size());
        try {
            log.debug("Hash encode was started");
            for (Long number : numbers) {
                if (number == null) {
                    log.error("Encoding Error number is null");
                    throw new IllegalArgumentException("Number can not be null");
                }
                byteBuffer.clear();
                byte[] bytes = base62.encode(byteBuffer.putLong(number).array());
                String hash = new String(bytes, StandardCharsets.UTF_8);
                if (hash.length() > hashLength) {
                    encodedHashes.add(hash.substring(0, hashLength));
                } else {
                    encodedHashes.add(hash);
                }
            }
            return encodedHashes;
        } catch (Exception e) {
            log.error("Encoding was failed for numbers: {}", numbers, e);
            throw new EncodingException("Base62 Error", e);
        } finally {
            byteBufferThreadLocal.remove();
            log.debug("Hash encode was finished");
        }
    }
}