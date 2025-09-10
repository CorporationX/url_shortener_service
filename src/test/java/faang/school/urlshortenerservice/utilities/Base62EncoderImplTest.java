package faang.school.urlshortenerservice.utilities;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
@AllArgsConstructor
public class Base62EncoderImplTest {
    @Mock
    private Base62EncoderImpl encoder = new Base62EncoderImpl();
    @Test
    void testEncodeSingleNumber(){
        Long inputNumber = 123L;
        String expected = "B9";
        assertEquals(expected, encoder.coding(inputNumber), "Тest for one number was not passed");

    }
    @Test
    void testEncodingString(){
        List<Long> numbers = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L);
        List<String> expected = List.of("B","C", "D","E", "F", "G","H" );
        assertEquals(expected, encoder.encode(numbers), "Test for coding entire list was not passed");
    }
}
