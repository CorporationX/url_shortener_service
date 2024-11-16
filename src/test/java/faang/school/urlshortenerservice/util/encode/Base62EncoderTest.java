package faang.school.urlshortenerservice.util.encode;

import org.junit.jupiter.api.Test;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static faang.school.urlshortenerservice.test.utils.TestData.NUMBERS;
import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder();

    @Test
    void testEncode_successful() {
        assertThat(base62Encoder.encode(NUMBERS))
                .isNotNull()
                .isEqualTo(HASHES);
    }
}
