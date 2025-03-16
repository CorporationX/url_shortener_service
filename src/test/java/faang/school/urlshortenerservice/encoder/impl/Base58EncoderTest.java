package faang.school.urlshortenerservice.encoder.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Base58EncoderTest {

    @InjectMocks
    private Base58Encoder base58Encoder;

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
    @DisplayName("Test Base58 Encoding")
    void testBase58Encode() {
        assertEquals("NX2KAG", base58Encoder.encode(number1));
        assertEquals("j34dKX", base58Encoder.encode(number2));
        assertEquals("LUXDDE", base58Encoder.encode(number3));
        assertEquals("LUXEDE", base58Encoder.encode(number4));
    }

    @Test
    @DisplayName("Test Base58 Encoding List")
    void testBase58EncodeList() {
        List<Long> numbers = new ArrayList<>();
        numbers.add(number1);
        numbers.add(number2);
        numbers.add(number3);
        numbers.add(number4);
        List<String> result = base58Encoder.encode(numbers);

        assertEquals("NX2KAG", result.get(0));
        assertEquals("j34dKX", result.get(1));
        assertEquals("LUXDDE", result.get(2));
        assertEquals("LUXEDE", result.get(3));
        assertEquals(numbers.size(), result.size());
    }


}