package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
class UrlControllerTest {
    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getHash() throws Exception {
        String hash = "asd";

        when(urlService.searchUrl(hash)).thenReturn("https://www.example.com");

        mockMvc.perform(get("/" + hash)
                        .header("x-user-id", "12345"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("https://www.example.com"));
    }

    @Test
    void getShortUrl() throws Exception {
        UrlDto urlDto = new UrlDto("http://yandex.ru");

        when(urlService.saveNewHash(urlDto)).thenReturn("shortUrl123");

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(urlDto))
                        .header("x-user-id", "12345"))
                .andExpect(status().isCreated())
                .andExpect(content().string("http://localhost:8080/shortUrl123"));
    }
}