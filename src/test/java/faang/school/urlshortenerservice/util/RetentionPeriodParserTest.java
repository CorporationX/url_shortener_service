package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test cases of RetentionPeriodParserTest")
public class RetentionPeriodParserTest {

    private final RetentionPeriodParser periodParser = new RetentionPeriodParser();

    @Test
    @DisplayName("calculateExpiryDate - incorrect retention period format")
    public void testCalculateExpiryDateIncorrectFormat() {
        String expectedMessage = "Incorrect retention period format. Example: 1y, 2M, 3d";

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> periodParser.calculateExpiryDate("y1")
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("calculateExpiryDate - success")
    public void testCalculateExpiryDateSuccess() {
        LocalDate expectedDate = LocalDate.now();

        assertAll(
                () -> assertEquals(expectedDate.minusYears(1), periodParser.calculateExpiryDate("1y").toLocalDate()),
                () -> assertEquals(expectedDate.minusMonths(2), periodParser.calculateExpiryDate("2M").toLocalDate()),
                () -> assertEquals(expectedDate.minusDays(3), periodParser.calculateExpiryDate("3d").toLocalDate())
        );
    }
}
