package faang.school.urlshortenerservice.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Base62ConverterTest {
    private final Base62Converter converter = new Base62Converter();

    @Test
    void convertToBase62_shouldReturnCorrectStringForPositiveNumbers() {
        assertEquals("1", converter.convertToBase62(1));
        assertEquals("A", converter.convertToBase62(10));
        assertEquals("Z", converter.convertToBase62(35));
        assertEquals("a", converter.convertToBase62(36));
        assertEquals("z", converter.convertToBase62(61));
        assertEquals("10", converter.convertToBase62(62));
        assertEquals("1z", converter.convertToBase62(123));
        assertEquals("20", converter.convertToBase62(124));
    }
}
