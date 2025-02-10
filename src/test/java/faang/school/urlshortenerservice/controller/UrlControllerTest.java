package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Test
    void redirect_ReturnsOriginalUrl_WhenHashIsValid() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";

        when(urlService.getUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/" + hash)
                        .header("x-user-id", 1))
                .andExpect(status().isFound())
                .andExpect(content().string(originalUrl));
    }

    @Test
    void redirect_ReturnsNotFound_WhenHashIsInvalid() throws Exception {
        String hash = "invalidHash";

        when(urlService.getUrl(hash)).thenThrow(new UrlNotFoundException(anyString()));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/" + hash)
                        .header("x-user-id", 1))
                .andExpect(status().isNotFound());
    }
}