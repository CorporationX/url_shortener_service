package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    private static final String TEST_ALPHABET = "wellWellWellHereIsATestAlphabet";

    @InjectMocks
    private Base62Encoder encoder =  new Base62Encoder(TEST_ALPHABET);

    @Test
    public void testEncode() {
        List<Long> numbers = Arrays.asList(125L, 256L, 512L);
        List<String> encodedStrings = encoder.encode(numbers);
        assertNotNull(encodedStrings);
        assertEquals(3, encodedStrings.size());
    }

    @Test
    void testEncodeNumbers() {
        Base62Encoder base62Encoder = new Base62Encoder();
        List<Long> numbers = Arrays.asList(1L, 22L, 2232323L);
        List<String> expected = Arrays.asList("1", "m", "dJm9");

        List<String> actual = base62Encoder.encodeNumbers(numbers);

        assertEquals(expected, actual);
    }
}
