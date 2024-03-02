package faang.school.urlshortenerservice.base62encoder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    Base62Encoder base62Encoder;
    private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    @Test
    void testEncoderSuccess() {
        String hash = base62Encoder.encoder(56800235584L);
        assertEquals("a000001", hash);
    }

    @Test
    void testEncodeSuccess() {
        List<String> hashes = base62Encoder.encodeList(List.of(56800235584L, 56800235585L));
        List<String> expectedHashes = List.of("a000001", "b000001");
        assertEquals(expectedHashes, hashes);
    }

}
