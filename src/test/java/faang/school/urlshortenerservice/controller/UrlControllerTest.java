package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.exception.GlobalExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlController urlController;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    private LongUrlDto longUrlDto;
    private ShortUrlDto shortUrlDto;

    @BeforeEach
    void setUp() {
        longUrlDto = new LongUrlDto("http:/reallyReallyLongUrl.com/something");
        shortUrlDto = new ShortUrlDto("http:/srt.com/abc123");
    }

    @Test
    @DisplayName("Short url created and returned success")
    void test_create_success() throws Exception {
        String jsonLongUrl = """
                {
                 "url" : "http:/reallyReallyLongUrl.com/something"
                }
                """;

        when(urlService.createShortUrl(longUrlDto)).thenReturn(shortUrlDto);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLongUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(jsonPath("$.shortUrl").value(shortUrlDto.shortUrl()));
    }

    @Test
    @DisplayName("Short url created and returned fail: blank long url provided")
    void test_create_fail_blankLongUrl() throws Exception {
        String jsonLongUrl = """
                {
                 "url" : ""
                }
                """;

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLongUrl))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("URL cannot be empty"))
                .andExpect(content().string(containsString("URL cannot be empty")));
    }

    @Test
    @DisplayName("Long URL returned success")
    void test_getUrl_whenValidInput_ReturnsDto() throws Exception {
        String shortUrl = "http:/srt.com/abc123";

        when(urlService.getUrl(shortUrl)).thenReturn(longUrlDto);

        mockMvc.perform(get("/api/v1/url")
                        .param("shortUrl", shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(jsonPath("$.url").value("http:/reallyReallyLongUrl.com/something"));
    }

    @Test
    @DisplayName("Test get real URL fail - provided empty param")
    void test_getUrl_WhenEmptyParam_ReturnsExceptionJson() throws Exception {
        String shortUrl = "";

        mockMvc.perform(get("/api/v1/url")
                        .param("shortUrl", shortUrl))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("URL cannot be empty")));
    }

    @Test
    @DisplayName("Test get real URL fail - no param provided")
    void test_getUrl_WhenENullParam_ReturnsExceptionJson() throws Exception {
        String shortUrl = null;

        mockMvc.perform(get("/api/v1/url")
                        .param("shortUrl", shortUrl))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Missed parameter")));
    }
}