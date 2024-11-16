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

    @Test
    public void testEncode() {
        List<String> hashes = List.of("8G", "9G", "AG", "BG", "CG", "DG", "EG", "FG", "GG", "HG", "IG");
        List<Long> range = List.of(1000L, 1001L, 1002L, 1003L, 1004L, 1005L, 1006L, 1007L, 1008L, 1009L, 1010L);

        List<String> result = base62Encoder.encode(range);

        assertEquals(result, hashes);
    }
}
