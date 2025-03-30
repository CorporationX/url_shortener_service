package faang.school.urlshortenerservice.dto;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.testcontainers.shaded.org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
class UrlDtoTest {
    private final String validHash = "abc123";
    private final LocalDateTime validDate = LocalDateTime.now();


    private String loadRegexFromYaml() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yaml");

        Map<String, String> application = yaml.load(inputStream);

        Object url = application.get("url");
        Object original = ((Map<String, String>) url).get("original");
        Object patterns = ((Map<String, String>) original).get("patterns");

        return String.valueOf(patterns);
    }

    private boolean isValidUrl(String url) {
        return url != null && url.matches(loadRegexFromYaml());
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
        UrlDto dto = new UrlDto(url, validHash, validDate);
        assertTrue(isValidUrl(dto.getUrl()),
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
        UrlDto dto = new UrlDto(url, validHash, validDate);
        assertFalse(isValidUrl(dto.getUrl()),
                "URL '" + url + "' must be invalid");
    }
}