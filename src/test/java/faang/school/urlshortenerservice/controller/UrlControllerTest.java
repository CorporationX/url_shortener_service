package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    public void testCreateShortUrl() throws Exception {
        String longUrl = "http://example.com/very/long/url";
        String shortUrl = "http://example.com/api/v1/url/abc123";
        UrlRequestDto urlRequestDto = new UrlRequestDto();
        urlRequestDto.setLongUrl(longUrl);

        when(urlService.createShortUrl(longUrl)).thenReturn(shortUrl);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(content().string(shortUrl));
    }

    @Test
    public void testRedirectToOriginalUrl() throws Exception {
        String hash = "someHash";
        String originalUrl = "http://example.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/url/" + hash))
            .andExpect(status().isFound())
            .andExpect(header().string("Location", originalUrl));

        verify(urlService, times(1)).getOriginalUrl(hash);
    }
}