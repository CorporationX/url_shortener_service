package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
        ReflectionTestUtils.setField(encoder, "encodeTemplate",
                                                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
        ReflectionTestUtils.setField(encoder, "baseLength", 62);
    }

    @Test
    void testGenerateHashes_ExactSixChars() {
        List<Long> numbers = Arrays.asList(
                916312072L,
                30000000000L,
                56800235583L
        );

        String[] hashes = encoder.generateHashes(numbers);
        for (String hash : hashes) {
            assertNotNull(hash);
            assertEquals(6, hash.length());
        }
    }
}
