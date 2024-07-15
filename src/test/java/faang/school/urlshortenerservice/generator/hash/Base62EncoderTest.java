package faang.school.urlshortenerservice.generator.hash;

import io.seruco.encoding.base62.Base62;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    @InjectMocks
    public Base62Encoder base62Encoder;

    @Mock
    public Base62 base62;

    @Test
    public void encodeTest() {
        List<Long> numbers = new ArrayList<>(List.of(1L, 2L, 3L));
        when(base62.encode("1".getBytes())).thenReturn("n".getBytes());
        when(base62.encode("2".getBytes())).thenReturn("o".getBytes());
        when(base62.encode("3".getBytes())).thenReturn("p".getBytes());

        List<String> expected = new ArrayList<>(List.of("n", "o", "p"));
        List<String> result = base62Encoder.encode(numbers);

        assertEquals(expected, result);
    }

}
