package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {

    private final Base62Encoder encoder = new Base62Encoder();

    @Test
    void testEncodeZero() {
        List<String> result = encoder.encode(List.of(0L));
        assertThat(result).containsExactly("0");
    }

    @Test
    void testEncodeSingleDigit() {
        List<String> result = encoder.encode(List.of(1L, 5L, 9L));
        assertThat(result).containsExactly("1", "5", "9");
    }

    @Test
    void testEncodeTwoDigits() {
        List<String> result = encoder.encode(List.of(10L, 62L, 63L));
        assertThat(result).containsExactly("a", "10", "11");
    }

    @Test
    void testEncodeBoundaryValues() {
        List<String> result = encoder.encode(List.of(61L, 62L, 3844L));
        assertThat(result).containsExactly("Z", "10", "100");
    }

    @Test
    void testEncodeEmptyList() {
        List<String> result = encoder.encode(List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void testEncodeUniqueness() {
        List<Long> numbers = List.of(1L, 2L, 3L, 100L, 200L);
        List<String> hashes = encoder.encode(numbers);
        
        assertThat(hashes).hasSize(numbers.size());
        assertThat(hashes).doesNotHaveDuplicates();
    }
}

