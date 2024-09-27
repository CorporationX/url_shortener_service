package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder encoder;

    @Test
    public void testEncode() {
        List<Long> numbers = List.of(10L, 124L);

        List<String> result = encoder.encode(numbers);

        assertEquals(List.of("A", "02"), result);
    }
}
