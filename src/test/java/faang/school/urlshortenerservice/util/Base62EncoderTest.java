package faang.school.urlshortenerservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class Base62EncoderTest {
    private final Base62Encoder base62Encoder = new Base62Encoder("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ");

    @Test
    @DisplayName("Create list of Strings from list of Long.")
    public void encode_whenFromListOfLong_returnListOfStrings() {
        List<Long> numbers = List.of(100L, 2L, 3L);
        List<String> expected = List.of("1C", "2", "3");
        List<String> actual = base62Encoder.encode(numbers);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return enpty list of strings when input is null.")
    public void encode_whenInputNull_returnEmptyList() {
        List<Long> numbers = null;
        List<String> expected = List.of();
        List<String> actual = base62Encoder.encode(numbers);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Return enpty list of strings when input is empty list.")
    public void encode_whenInputEmptyList_returnEmptyList() {
        List<Long> numbers = List.of();
        List<String> expected = List.of();
        List<String> actual = base62Encoder.encode(numbers);

        assertEquals(expected, actual);
    }
}
