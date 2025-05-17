package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @Test
    void testShortenUrlSuccess() throws Exception {
        String shortUrl = "http://short.url/abc123";
        when(urlService.shortenUrl("https://example.com")).thenReturn(shortUrl);

        MvcResult result = mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"https://example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response body: " + responseContent);
    }

    @Test
    void testShortenUrlValidationError() throws Exception {
        mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.url").isArray())
                .andExpect(jsonPath("$.url", hasItem("URL cannot be empty")))
                .andExpect(jsonPath("$.url", hasItem("Invalid URL format")));
    }

    @Test
    void testRedirectToOriginalUrlSuccess() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/url/{hash}", hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl))
                .andExpect(content().string(""));
    }

    @Test
    void testRedirectToOriginalUrlNotFound() throws Exception {
        String hash = "invalidHash";
        when(urlService.getOriginalUrl(hash)).thenThrow(new UrlNotFoundException("URL not found for hash: " + hash));

        mockMvc.perform(get("/url/{hash}", hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("URL not found for hash: " + hash));
    }
}