package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.util.Base62Encoder;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Base62EncoderTest {

    private Base62Encoder encoder = new Base62Encoder();

    @Test
    void testEncode_Ok(){
        ReflectionTestUtils.setField(encoder, "BASE62",
                "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(encoder, "base62Length", 62);
        List<String> hashes = encoder.encode(List.of(1235L, 6545L, 99999999L));

        assertEquals(3, hashes.size());
        assertTrue(hashes.get(0).length() < 6);
        assertTrue(hashes.get(1).length() < 6);
        assertTrue(hashes.get(2).length() < 6);
    }
}
