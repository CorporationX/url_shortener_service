package faang.school.urlshortenerservice.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UrlRequestDtoTest {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testValidUrl() {
        UrlRequestDto dto = new UrlRequestDto("https://example.com");
        Set<ConstraintViolation<UrlRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "No violations should be present for a valid URL");
    }

    @Test
    void testInvalidUrl() {
        UrlRequestDto dto = new UrlRequestDto("invalid-url");
        Set<ConstraintViolation<UrlRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Violations should be present for an invalid URL");

        ConstraintViolation<UrlRequestDto> violation = violations.iterator().next();
        assertEquals("Incorrect URL format", violation.getMessage(),
                "The error message should match the one specified in the @URL annotation");

        UrlRequestDto dto1 = new UrlRequestDto("htp:/test");
        Set<ConstraintViolation<UrlRequestDto>> violations1 = validator.validate(dto1);
        assertFalse(violations1.isEmpty(), "Violations should be present for an invalid URL");

        ConstraintViolation<UrlRequestDto> violation1 = violations1.iterator().next();
        assertEquals("Incorrect URL format", violation1.getMessage(),
                "The error message should match the one specified in the @URL annotation");

    }

    @Test
    void testEmptyUrl() {
        UrlRequestDto dto = new UrlRequestDto("");
        Set<ConstraintViolation<UrlRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty(), "Violations should be present for an empty URL");

        ConstraintViolation<UrlRequestDto> violation = violations.iterator().next();
        assertEquals("не должно быть пустым", violation.getMessage(), "The error message should match the one specified in the @NotBlank annotation");
    }

}