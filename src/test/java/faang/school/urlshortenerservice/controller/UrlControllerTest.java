package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.exeption.GlobalExceptionHandler;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlController urlController;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    private LongUrlDto longUrlDto;
    private String shortUrl;

    @BeforeEach
    void setUp() {
        longUrlDto = new LongUrlDto("http:/reallyReallyLongUrl.com/something");
        shortUrl = "https:/localhost:8099/abc123";
    }

    @Test
    @DisplayName("Short url created and returned success")
    void test_create_success() throws Exception {
        String jsonLongUrl = """
                {
                 "url" : "http:/reallyReallyLongUrl.com/something"
                }
                """;

        when(urlService.createShortUrl(longUrlDto)).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLongUrl))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortUrl));
    }

    @Test
    @DisplayName("Short url created and returned fail: blank long url provided")
    void test_create_fail_blankLongUrl() throws Exception {
        String jsonLongUrl = """
                {
                 "url" : ""
                }
                """;

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonLongUrl))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").value("URL cannot be empty"))
                .andExpect(content().string(containsString("URL cannot be empty")));
    }

    @Test
    @DisplayName("Long URL returned success")
    void test_getUrl_whenValidInput_ReturnsDto() throws Exception {
        String hash = "abc123";
        String longUrl = "https://google.com";

        when(urlService.getUrl(hash)).thenReturn(longUrl);

        mockMvc.perform(get("/{hash}", hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues(longUrl));
    }

    @Test
    @DisplayName("Test get real URL fail - provided empty path")
    void test_getUrl_WhenEmptyPath_ReturnsNotFound() throws Exception {
        String hash = "";

        mockMvc.perform(get("/{hash}", hash))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test get real URL fail - hash size is too long")
    void test_getUrl_WhenPathMore6_ReturnsExceptionJson() throws Exception {
        String hash = "1234567";

        mockMvc.perform(get("/{hash}", hash))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid hash size")));
    }
}
