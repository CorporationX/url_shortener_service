package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
        // Устанавливаем значения через ReflectionTestUtils, так как @Value не работает в unit тестах
        ReflectionTestUtils.setField(base62Encoder, "base62Alphabet", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        ReflectionTestUtils.setField(base62Encoder, "base", 62);
    }

    @Test
    void testEncodeZero() {
        List<Long> numbers = List.of(0L);
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("0");
    }

    @Test
    void testEncodeSingleDigit() {
        List<Long> numbers = List.of(1L, 10L, 61L);
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("1");
        assertThat(result.get(1)).isEqualTo("A");
        assertThat(result.get(2)).isEqualTo("z");
    }

    @Test
    void testEncodeMultipleNumbers() {
        List<Long> numbers = Arrays.asList(1L, 62L, 100L, 1000L);
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).hasSize(4);
        assertThat(result.get(0)).isEqualTo("1");
        assertThat(result.get(1)).isEqualTo("10");
        assertThat(result.get(2)).isEqualTo("1c");
        assertThat(result.get(3)).isEqualTo("G8");
    }

    @Test
    void testEncodeLargeNumber() {
        List<Long> numbers = List.of(123456789L);
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isNotEmpty();
        assertThat(result.get(0).length()).isLessThanOrEqualTo(6);
    }

    @Test
    void testEncodeEmptyList() {
        List<Long> numbers = List.of();
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).isEmpty();
    }

    @Test
    void testEncodeProducesUniqueHashes() {
        List<Long> numbers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).hasSize(5);
        assertThat(result).doesNotHaveDuplicates();
    }

    @Test
    void testEncodeLargeList() {
        List<Long> numbers = Arrays.asList(
                1L, 100L, 1000L, 10000L, 100000L
        );
        List<String> result = base62Encoder.encode(numbers);

        assertThat(result).hasSize(5);
        assertThat(result).allMatch(hash -> hash.length() <= 6);
    }
}