package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.service.base62Encoder.Base62Encoder;
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
    void testGenerateHashList() {
        List<Long> numbers = List.of(1L, 36L, 62L, 3843L, 3844L);
        List<String> expectedHashes = List.of("1", "a", "01", "zz", "001");

        assertEquals(expectedHashes, base62Encoder.generateHashList(numbers));
    }
}
