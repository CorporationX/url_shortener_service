package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    void encodeTest() {
        List<Long> numbers = List.of(1234L, 9999L, 56789L, 123456L);
        List<String> expectedHashes = List.of("Ju", "2bH", "Elx", "W7E");

        List<String> hashes = base62Encoder.encode(numbers);
        assertEquals(expectedHashes, hashes);
    }
}