package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    public void testSingleNumberEncoding() {
        Long number = 0L;
        String encoded = encoder.encode(Arrays.asList(number)).get(0);
        Assertions.assertEquals("0", encoded);

        number = 1L;
        encoded = encoder.encode(Arrays.asList(number)).get(0);
        Assertions.assertEquals("1", encoded);

        number = 61L;
        encoded = encoder.encode(Arrays.asList(number)).get(0);
        Assertions.assertEquals("z", encoded);

        number = 62L;
        encoded = encoder.encode(Arrays.asList(number)).get(0);
        Assertions.assertEquals("01", encoded);

        number = 123456789L;
        encoded = encoder.encode(Arrays.asList(number)).get(0);
        Assertions.assertEquals("Xk0M8", encoded);
    }

    @Test
    public void testMultipleNumberEncoding() {
        List<Long> numbers = Arrays.asList(0L, 1L, 61L, 62L, 123456789L);
        List<String> encoded = encoder.encode(numbers);

        Assertions.assertEquals("0", encoded.get(0));
        Assertions.assertEquals("1", encoded.get(1));
        Assertions.assertEquals("z", encoded.get(2));
        Assertions.assertEquals("01", encoded.get(3));
        Assertions.assertEquals("Xk0M8", encoded.get(4));
    }

    @Test
    public void testEncodingWithEmptyList() {
        List<Long> numbers = Arrays.asList();
        List<String> encoded = encoder.encode(numbers);
        Assertions.assertTrue(encoded.isEmpty());
    }

    @Test
    public void testLargeNumberEncoding() {
        Long number = Long.MAX_VALUE;
        String encoded = encoder.encode(Arrays.asList(number)).get(0);
        Assertions.assertNotNull(encoded);
        Assertions.assertTrue(encoded.length() > 0);
    }

}