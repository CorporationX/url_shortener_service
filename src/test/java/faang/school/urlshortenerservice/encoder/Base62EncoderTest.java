package faang.school.urlshortenerservice.encoder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private Base62Encoder encoder;

    @BeforeEach
    void setUp() {
        encoder = new Base62Encoder();
        ReflectionTestUtils.setField(encoder, "MAX_HASH_LENGTH", 8);
    }

    @Test
    @DisplayName("Кодирование положительных чисел")
    void encodePositiveNumbers() {
        List<Long> input = new ArrayList<>(List.of(123L, 456L, 789L));
        List<String> result = encoder.encode(input);

        assertEquals(List.of("1z", "7M", "Cj"), result);
    }

    @Test
    @DisplayName("Кодирование с обрезанием хэша при превышении длины")
    void encodeTruncatesWhenExceedsMaxLength() {
        ReflectionTestUtils.setField(encoder, "MAX_HASH_LENGTH", 3);
        List<Long> input = new ArrayList<>(List.of(999999999999L));

        List<String> result = encoder.encode(input);

        assertEquals(3, result.get(0).length());
    }

    @Test
    @DisplayName("Кодирование нуля возвращает пустую строку")
    void encodeZeroReturnsEmpty() {
        List<Long> input = new ArrayList<>(List.of(0L));
        List<String> result = encoder.encode(input);

        assertEquals(List.of(""), result);
    }

    @Test
    @DisplayName("Кодирование отрицательных чисел - они фильтруются")
    void encodeFiltersNegativeNumbers() {
        List<Long> input = new ArrayList<>(List.of(-1L, -100L, 42L));
        List<String> result = encoder.encode(input);

        assertEquals(List.of("g"), result);
    }

    @Test
    @DisplayName("Кодирование пустого списка возвращает пустой список")
    void encodeEmptyListReturnsEmpty() {
        List<Long> input = new ArrayList<>();
        List<String> result = encoder.encode(input);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Кодирование гарантирует уникальность для разных чисел")
    void encodeProducesUniqueResults() {
        List<Long> input = new ArrayList<>(List.of(12345L, 54321L));
        List<String> result = encoder.encode(input);

        assertNotEquals(result.get(0), result.get(1));
    }
}
