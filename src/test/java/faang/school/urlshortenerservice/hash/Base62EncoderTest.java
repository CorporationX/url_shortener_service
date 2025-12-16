package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    public void encode_EncodesSuccessfully() {
        List<Long> anyNumbers = List.of(1L, 61L, 62L, 123L);

        assertEquals(List.of("1", "z", "10", "1z"), base62Encoder.encode(anyNumbers));
    }
}
