package faang.school.urlshortenerservice.encoder;

import faang.school.urlshortenerservice.model.hash.Hash;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {

    private static final long FIRST_VALUE = 10_000_000_000L;
    private static final long SECOND_VALUE = 33_444_555_123L;
    private static final String FIRST_RESULT = "KY8U4k";
    private static final String SECOND_RESULT = "BK6xFK";

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    @DisplayName("When list of numbers passed encode it using Base62 algorithm and return back list of Hashes")
    public void whenNumbersPassedThenEncodeItWithBase62ThenReturnHashList() {
        List<Hash> hashes = base62Encoder.encode(List.of(FIRST_VALUE, SECOND_VALUE));
        assertEquals(hashes.get(0).getHash(), FIRST_RESULT);
        assertEquals(hashes.get(1).getHash(), SECOND_RESULT);
    }
}
