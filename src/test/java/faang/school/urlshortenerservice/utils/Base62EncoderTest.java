package faang.school.urlshortenerservice.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class Base62EncoderTest {

    @Test
    public void testBase62Encoding() {
        Base62Encoder base62Encoder = new Base62Encoder();
        List<Long> numbers = Arrays.asList(1L, 2L, 13L, 764L, 121_637L);
        List<String> results = Arrays.asList("b", "c", "n", "um", "3NF");
        assertThat(base62Encoder.encode(numbers)).isEqualTo(results);
    }
}