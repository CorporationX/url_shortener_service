package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UrlService urlService;

    @Test
    void testCreateShortUrl() throws Exception {
        when(urlService.createShortUrl("http://example.com", 1L)).thenReturn("abc123");

        mockMvc.perform(post("/url")
                        .param("url", "http://example.com")
                        .header("x-user-id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void testRedirect() throws Exception {
        when(urlService.getOriginalUrl("abc123")).thenReturn("http://example.com");

        mockMvc.perform(get("/abc123"))
                .andExpect(status().isMovedPermanently())
                .andExpect(header().string("Location", "http://example.com"));
    }
}