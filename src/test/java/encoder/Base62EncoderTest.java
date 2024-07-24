package encoder;

import faang.school.urlshortenerservice.generator.encoder.Base62Encoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    @DisplayName("Test the algorithm for correct operation.")
    public void testEncode() {
        Long number = 12345L;
        String expected = "dnh";
        String actual = base62Encoder.encode(number);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Test algorithm with collection.")
    public void testEncodeCollections() {
        List<Long> nums = Arrays.asList(12345L, 67890L, 13579L);
        List<String> expected = Arrays.asList("dnh", "rPa", "dHb");
        List<String> actual = base62Encoder.encodeCollection(nums);
        assertEquals(expected, actual);
    }
}