package faang.school.urlshortenerservice.service.generator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {
    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testEncode() {
        List<Long> range = List.of(1000L, 1001L, 1002L, 1003L, 1004L, 1005L, 1006L, 1007L, 1008L, 1009L, 1010L);
        List<String> expected = List.of("8G", "9G", "AG", "BG", "CG", "DG", "EG", "FG", "GG", "HG", "IG");

        List<String> result = encoder.encode(range);

        assertEquals(result, expected);
    }
}