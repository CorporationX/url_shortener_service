package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.handler.UrlExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@ContextConfiguration(classes = {UrlController.class})
@Import(UrlExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    private final String testUrl = "https://example.com";
    private final String testHash = "abc123";

    @Test
    void testCreateShortUrlSuccessfully() throws Exception {
        when(urlService.getHash(testUrl)).thenReturn(testHash);

        mockMvc.perform(post("/api/v1/shortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + testUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shortUrl").value("short.url/abc123"))
                .andExpect(jsonPath("$.originalUrl").value(testUrl))
                .andExpect(jsonPath("$.hash").value("abc123"));
    }

    @Test
    void testCreateShortUrlWhenInvalidUrl() throws Exception {
        mockMvc.perform(post("/api/v1/shortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid-url\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRedirectToOriginalUrlSuccessfully() throws Exception {
        when(urlService.getOriginalUrl(testHash)).thenReturn(testUrl);

        mockMvc.perform(get("/api/v1/shortener/" + testHash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", testUrl));
    }

    @Test
    void testRedirectToOriginalUrlWhenNotFound() throws Exception {
        when(urlService.getOriginalUrl(testHash)).thenThrow(new UrlNotFoundException("Hash not found: " + testHash));

        mockMvc.perform(get("/api/v1/shortener/" + testHash))
                .andExpect(status().isNotFound());
    }
}
