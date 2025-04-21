package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.hash.Base62Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void shouldEncodeZero() {
        assertEquals("A", encoder.encodeNumber(0));
    }

    @Test
    void shouldEncodeSimpleNumbers() {
        assertEquals("B", encoder.encodeNumber(1));
        assertEquals("9", encoder.encodeNumber(61));
        assertEquals("AB", encoder.encodeNumber(62));
    }

    @Test
    void shouldEncodeListOfNumbers() {
        List<String> result = encoder.encode(List.of(0L, 1L, 62L));
        assertThat(result).containsExactly("A", "B", "AB");
    }
}
