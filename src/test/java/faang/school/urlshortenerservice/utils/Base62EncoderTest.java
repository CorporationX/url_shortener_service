package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    void testEncode() {
        List<Long> numbers = Arrays.asList(1L, 62L, 123L, 999L);
        List<String> expectedEncoded = Arrays.asList("1", "10", "1z", "G7");

        List<HashEntity> encodedEntities = base62Encoder.encode(numbers);

        assertEquals(numbers.size(), encodedEntities.size());

        for (int i = 0; i < numbers.size(); i++) {
            assertEquals(expectedEncoded.get(i), encodedEntities.get(i).getHash());
        }
    }
}