package faang.school.urlshortenerservice.controller;


import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exception.GlobalExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    private String url;
    private String hash;
    private String shortUrl;

    @BeforeEach
    public void setUp() {
        url = "https://faang-school.com/courses";
        hash = "777";
        shortUrl = "https://localhost:8077/777";
    }

    @Test
    @DisplayName("Create short link from original link: success case")
    void testCreateShortLink_Success() throws Exception {
        when(urlService.createShortLink(url)).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(url))
                .andExpect(status().isCreated())
                .andExpect(content().string(shortUrl));
    }

    @Test
    @DisplayName("Create short link from original link: notUrl")
    void testCreateShortLink_notUrl() throws Exception {
        url = "not url";
        when(urlService.createShortLink(url)).thenReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(url))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").exists())
                .andExpect(jsonPath("$.url").value("must be a valid URL"));
    }

    @Test
    @DisplayName("Get original link: success case")
    void testGetOriginalLink_Success() throws Exception {
        when(urlService.getOriginalUrl(hash)).thenReturn(url);

        mockMvc.perform(get(String.format("/%s", hash))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hash))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(header().string("Location", url));
    }

    @Test
    @DisplayName("Get original link: not found")
    void testGetOriginalLink_NotFound() throws Exception {
        when(urlService.getOriginalUrl(hash)).thenThrow(new EntityNotFoundException(String.format("Url with hash %s not found", hash)));

        mockMvc.perform(get(String.format("/%s", hash))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hash))
                .andExpect(status().isNotFound());
    }
}
