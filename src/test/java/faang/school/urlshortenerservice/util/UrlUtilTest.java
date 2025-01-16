package faang.school.urlshortenerservice.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class UrlUtilTest {

    @InjectMocks
    private UrlUtil urlUtil;

    @Test
    void isValidUrlTrueTest() {
        String validUrl = "http://example.com/valid";
        assertTrue(urlUtil.isValidUrl(validUrl));
    }

    @Test
    void isValidUrlFalseTest() {
        String firstInvalidUrl = "http://example.com/valid^";
        String secondInvalidUrl = "htps://example.com/valid";
        assertFalse(urlUtil.isValidUrl(firstInvalidUrl));
        assertFalse(urlUtil.isValidUrl(secondInvalidUrl));
    }

    @Test
    public void testEnsureUrlHasProtocol() {
        String url1 = "http://example.com";
        String result1 = urlUtil.ensureUrlHasProtocol(url1);
        assertEquals(url1, result1);

        String url2 = "https://example.com";
        String result2 = urlUtil.ensureUrlHasProtocol(url2);
        assertEquals(url2, result2);

        String url3 = "example.com";
        String expected = "http://example.com";
        String result3 = urlUtil.ensureUrlHasProtocol(url3);
        assertEquals(expected, result3);
    }

    @Test
    void buildShortUrlFromContextTest() {
        ReflectionTestUtils.setField(urlUtil, "endpointBasePath", "/api/v1/url");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setServerName("localhost");
        request.setServerPort(8080);
        String hash = "abc123";
        String expectedShortUrl = "http://localhost:8080/api/v1/url/abc123";

        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequestUri(request);
        try (MockedStatic<ServletUriComponentsBuilder> mockedServletClass = Mockito.mockStatic(ServletUriComponentsBuilder.class)) {
            mockedServletClass.when(ServletUriComponentsBuilder::fromCurrentContextPath).thenReturn(builder);
            String shortUrl = urlUtil.buildShortUrlFromContext(hash);
            assertEquals(expectedShortUrl, shortUrl);
        }
    }
}