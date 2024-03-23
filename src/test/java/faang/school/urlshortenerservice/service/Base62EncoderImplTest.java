package faang.school.urlshortenerservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class Base62EncoderImplTest {
    @InjectMocks
    private Base62EncoderImpl base62Encoder;

    @Test
    void encodeSingleNumber() {
        long numberToEncode = 123L;
        String expectedEncodedValue = "1Z";
        String actualEncodedValue = base62Encoder.encode(numberToEncode);

        assertEquals(expectedEncodedValue, actualEncodedValue);
    }

    @Test
    void encodeListOfNumbers() {
        List<Long> numbersToEncode = LongStream.rangeClosed(1, 5)
                .boxed()
                .toList();
        List<String> expectedEncodedValues = List.of("1", "2", "3", "4", "5");
        List<String> actualEncodedValues = base62Encoder.encode(numbersToEncode);

        assertEquals(expectedEncodedValues, actualEncodedValues);
    }

}