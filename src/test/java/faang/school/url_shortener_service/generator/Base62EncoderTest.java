package faang.school.url_shortener_service.generator;

import faang.school.url_shortener_service.entity.Hash;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Base62EncoderTest {

    private Base62Encoder base62Encoder;

    @BeforeEach
    void setUp() {
        base62Encoder = new Base62Encoder();
    }

    @Test
    void encode_ShouldReturnCorrectBase62Hashes_WhenNumbersAreGiven() {
        List<Long> numbers = List.of(1L, 10L, 100L, 1000L);
        List<Hash> hashes = base62Encoder.encode(numbers);

        assertThat(hashes).hasSize(4);
        assertThat(hashes.get(0).getHash()).isEqualTo("1");
        assertThat(hashes.get(1).getHash()).isEqualTo("A");
        assertThat(hashes.get(2).getHash()).isEqualTo("1c");
        assertThat(hashes.get(3).getHash()).isEqualTo("G8");
    }

    @Test
    void encode_ShouldReturnEmptyList_WhenInputListIsEmpty() {
        List<Hash> hashes = base62Encoder.encode(List.of());
        assertThat(hashes).isEmpty();
    }

    @Test
    void encode_ShouldReturnSameHash_ForSameInputNumbers() {
        List<Long> numbers = List.of(123L, 123L, 456L);
        List<Hash> hashes = base62Encoder.encode(numbers);

        assertThat(hashes).hasSize(3);
        assertThat(hashes.get(0).getHash()).isEqualTo(hashes.get(1).getHash());
        assertThat(hashes.get(2).getHash()).isNotEqualTo(hashes.get(0).getHash());
    }

    @Test
    void encode_ShouldReturnEmptyString_WhenNumberIsZero() {
        List<Hash> hashes = base62Encoder.encode(List.of(0L));

        assertThat(hashes).hasSize(1);
        assertThat(hashes.get(0).getHash()).isEmpty();
    }
}