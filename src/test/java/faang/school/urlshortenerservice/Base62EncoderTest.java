package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.Base62Encoder;
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

    private final List<String> hashes = List.of(
            "sZmu4u",
            "sZmu4v",
            "sZmu4w",
            "sZmu4x",
            "sZmu4y",
            "sZmu4z",
            "sZmu50",
            "sZmu51",
            "sZmu52",
            "sZmu53",
            "sZmu54");

    private final List<Long> range = List.of(
            50_000_000_000L,
            50_000_000_001L,
            50_000_000_002L,
            50_000_000_003L,
            50_000_000_004L,
            50_000_000_005L,
            50_000_000_006L,
            50_000_000_007L,
            50_000_000_008L,
            50_000_000_009L,
            50_000_000_010L
    );

    @Test
    public void testEncode() {
        List<String> result = base62Encoder.encode(range);

        assertEquals(result, hashes);
    }
}
