package faang.school.urlshortenerservice.encoder.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {

    @InjectMocks
    private Base62Encoder base62Encoder;

    private Long number1;
    private Long number2;
    private Long number3;
    private Long number4;
    @BeforeEach
    void setUp() {
        number1 = 1000000000L;
        number2 = 2000000000L;
        number3 = 20000000000L;
        number4 = 20000000001L;
    }

    @Test
    @DisplayName("Test Base62 Encoding")
    void testBase62Encode() {
        assertEquals("f51Ggt", base62Encoder.encode(number1));
        assertEquals("LB2WMn", base62Encoder.encode(number2));
        assertEquals("VpLAdx", base62Encoder.encode(number3));
        assertEquals("VpLBdx", base62Encoder.encode(number4));
    }

    @Test
    @DisplayName("Test Base62 Encoding List")
    void testBase62EncodeList() {
        List<Long> numbers = new ArrayList<>();
        numbers.add(number1);
        numbers.add(number2);
        numbers.add(number3);
        numbers.add(number4);
        List<String> result = base62Encoder.encode(numbers);

        assertEquals("f51Ggt", result.get(0));
        assertEquals("LB2WMn", result.get(1));
        assertEquals("VpLAdx", result.get(2));
        assertEquals("VpLBdx", result.get(3));
        assertEquals(numbers.size(), result.size());
    }
}