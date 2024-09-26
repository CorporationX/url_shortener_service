package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testGetShortUrl() throws Exception {
        UrlDto urlDto = new UrlDto("hash", "https://youtube.com");
        when(urlService.getShortUrl(urlDto)).thenReturn("shortUrl");

        mockMvc.perform(post("/shortener/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isOk())
                .andExpect(content().string("shortUrl"));
    }

    @Test
    void testGetOriginalLink() throws Exception {
        String hash = "hash";
        String originalLink = "https://youtube.com";

        when(urlService.getOriginalLink(hash)).thenReturn(originalLink);

        mockMvc.perform(get("/shortener/" + hash))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, originalLink));
    }

}