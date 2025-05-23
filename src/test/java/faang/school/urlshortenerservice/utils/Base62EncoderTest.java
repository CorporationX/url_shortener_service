package faang.school.urlshortenerservice.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@ExtendWith(MockitoExtension.class)
@DisplayName("Тест Base62Encoder")
public class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    @Nested
    @DisplayName("Кодирование списка чисел")
    class EncodeBatchTests {

        @Test
        @DisplayName("Получаем список закодированных чисел")
        void givenMultipleNumbers_whenEncode_thenCorrect() {
            List<Long> input = List.of(1L, 10L, 61L, 62L, 100L, 1000L);

            List<String> result = base62Encoder.encodeNumbers(input);

            assertThat(result).containsExactly("1", "A", "z", "10", "1c", "G8");
        }
    }
}
