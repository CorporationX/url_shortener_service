package faang.school.urlshortenerservice.util.encode;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static faang.school.urlshortenerservice.test.utils.TestData.NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {
    private static final String CHARSET = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz1032547698";
    private static final int BASE = CHARSET.length();

    private final Base62Encoder base62Encoder = new Base62Encoder();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(base62Encoder, "characters", CHARSET);
        ReflectionTestUtils.setField(base62Encoder, "base", BASE);
    }

    @Test
    void testEncode_successful() {
        assertThat(base62Encoder.encode(NUMBERS))
                .isNotNull()
                .isEqualTo(HASHES);
    }
}