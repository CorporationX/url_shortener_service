package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

class Base62EncoderTest {

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    public void testEncode() {
        List<String> expected = List.of("WG", "12W", "1Ym");
        List<String> actualList = base62Encoder.encode(List.of(2000L, 4000L, 6000L));

        assertLinesMatch(expected, actualList);
        assertEquals(3, actualList.size());
    }
}