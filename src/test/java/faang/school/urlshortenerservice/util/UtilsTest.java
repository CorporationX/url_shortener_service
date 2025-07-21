package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UtilsTest {

    private final Utils utils = new Utils();

    // Тестирование метода format()
    @Test
    void testFormatWithOneArgument() {
        assertEquals("Hello, John!",
            utils.format("Hello, {}!", "John"));
    }

    @Test
    void testFormatWithTwoArguments() {
        assertEquals("You have 10 apples.",
            utils.format("{} have {} {}.", "You", 10, "apples"));
    }

    @Test
    void testFormatWithMultipleArguments() {
        assertEquals("I want to buy a red car for $5000.",
            utils.format("I want to buy a {} {} for ${}.", "red", "car", 5000));
    }

    // Тестирование метода isUrlValid()
    @Test
    void testIsUrlValidSuccess() {
        assertTrue(utils.isUrlValid("https://www.example.com"));
    }

    @Test
    void testIsUrlValidFailure() {
        assertFalse(utils.isUrlValid("htt://invalid.url"));
    }

    @Test
    void testIsUrlValidEmptyString() {
        assertFalse(utils.isUrlValid(""));
    }
}