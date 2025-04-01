package faang.school.urlshortenerservice.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UrlDtoTest {

    private final String patterns = "^(https?://)?(localhost|\\d{1,3}(\\.\\d{1,3}){3}|" +
            "([a-zA-Z0-9]+(-[a-zA-Z0-9]+)*\\.)+[a-zA-Z]{2,})(:\\d+)?(/\\S*)?$";

    private boolean isValidUrl(String url) {
        return url != null && url.matches(patterns);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://example.com",
            "http://test.org/path?query=param",
            "example.com",
            "sub.domain.co.us",
            "http://localhost",
            "https://192.168.1.1"
    })
    void shouldAcceptValidUrls(String url) {
        UrlDto dto = new UrlDto(url);
        assertTrue(isValidUrl(dto.url()),
                "URL '" + url + "' must be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid-url",
            "javascript:alert(1)",
            "http://",
            "http://.com",
            "http://example..com",
            "http://-example.com",
            "http://example-.com"
    })
    void testShouldRejectInvalidUrls(String url) {
        UrlDto dto = new UrlDto(url);
        assertFalse(isValidUrl(dto.url()),
                "URL '" + url + "' must be invalid");
    }
}