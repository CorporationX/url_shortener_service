package faang.school.urlshortenerservice.service.util;
import faang.school.urlshortenerservice.util.Base62Encoder;
import org.junit.jupiter.api.Test;

import static faang.school.urlshortenerservice.service.data.TestData.HASHES;
import static faang.school.urlshortenerservice.service.data.TestData.NUMBERS;
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

