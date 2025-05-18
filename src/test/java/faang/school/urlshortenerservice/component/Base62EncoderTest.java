package faang.school.urlshortenerservice.component;

import faang.school.urlshortenerservice.exceptions.EmptyNumbersListException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    public void testPositiveEncode() {
        List<Long> numbers = new ArrayList<>();
        numbers.add(123L);
        numbers.add(456L);
        List<String> strings = base62Encoder.encode(numbers);
        assertEquals(strings.size(), numbers.size());
    }

    @Test
    public void testNegativeEncodeIsNull() {
        assertThrows(EmptyNumbersListException.class, () -> base62Encoder.encode(null));
    }

    @Test
    public void testPositiveEncodeIsEmpty() {
        assertThrows(EmptyNumbersListException.class, () -> base62Encoder.encode(new ArrayList<>()));
    }
}
