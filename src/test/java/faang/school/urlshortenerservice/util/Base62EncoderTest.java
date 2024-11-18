package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {
    private Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void encode() {
        List<String> hashes = base62Encoder.encode(List.of(1235L, 6545L, 99999999L));

        assertEquals(3, hashes.size());
        assertTrue(hashes.get(0).length() < 6);
        assertTrue(hashes.get(1).length() < 6);
        assertTrue(hashes.get(2).length() < 6);
    }
}