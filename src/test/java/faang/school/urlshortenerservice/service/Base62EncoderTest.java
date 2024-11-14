package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private Base62Encoder base62Encoder = new Base62Encoder();

    private static final long ONE = 150_000L;
    private static final long TWO = 2_000_000L;
    private static final String FIRST_HASH = "wbN";
    private static final String SECOND_HASH = "esyi";

    @Test
    @DisplayName("Success when encode list of numbers")
    public void whenEncodeThenReturnListHash() {
        List<Hash> result = base62Encoder.encode(List.of(ONE, TWO));

        assertNotNull(result);
        assertEquals(FIRST_HASH, result.get(0).getHash());
        assertEquals(SECOND_HASH, result.get(1).getHash());
    }
}