package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Test cases of RetentionPeriodParserTest")
public class RetentionPeriodParserTest {

    @Test
    @DisplayName("calculateExpiryDate - incorrect retention period format")
    public void testCalculateExpiryDateIncorrectFormat() {
        String expectedMessage = "Incorrect retention period format. Example: 1y, 2M, 3d";

        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> RetentionPeriodParser.calculateExpiryDate("y1")
        );

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("calculateExpiryDate - success")
    public void testCalculateExpiryDateSuccess() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        assertAll(
                () -> assertEquals(
                        now.minusYears(1).toLocalDate(),
                        RetentionPeriodParser.calculateExpiryDate("1y").atZone(ZoneOffset.UTC).toLocalDate()
                ),
                () -> assertEquals(
                        now.minusMonths(2).toLocalDate(),
                        RetentionPeriodParser.calculateExpiryDate("2M").atZone(ZoneOffset.UTC).toLocalDate()
                ),
                () -> assertEquals(
                        now.minusDays(3).toLocalDate(),
                        RetentionPeriodParser.calculateExpiryDate("3d").atZone(ZoneOffset.UTC).toLocalDate()
                )
        );
    }
}
