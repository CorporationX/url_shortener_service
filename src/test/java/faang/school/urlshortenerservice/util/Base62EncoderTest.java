package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    private static final long ONE_HUNDRED = 100;
    private static final long TWO_HUNDRED = 200;

    private static final String ENCODED_BASE_62_ONE = "1c";
    private static final String ENCODED_BASE_62_TWO = "3E";

    @InjectMocks
    private Base62Encoder base62Encoder;

    @Test
    @DisplayName("When value passed on then return expected encoded in base 62 value")
    void whenValuePassedThenReturnEncodedValue() {
        assertEquals(ENCODED_BASE_62_ONE, base62Encoder.encodeNumberInBase62(ONE_HUNDRED));
    }

    @Test
    @DisplayName("When list of values passed on then return expected encoded in base 62 values")
    void whenListValuesPassedThenReturnEncodedValues() {
        List<Long> numbers = List.of(ONE_HUNDRED, TWO_HUNDRED);
        List<String> numbersInBase62 = List.of(ENCODED_BASE_62_ONE, ENCODED_BASE_62_TWO);

        assertEquals(numbersInBase62, base62Encoder.encodeNumberListInBase62(numbers));
    }
}