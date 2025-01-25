package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private ObjectMapper objectMapper;

    private UrlDto urlDto;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
        objectMapper = new ObjectMapper();
        urlDto = UrlDto.builder().url("https://www.google.com").build();
    }


    @Test
    void testGetOriginalUrl() throws Exception {
        // Arrange
        String hash = "abc123";
        String originalUrl = "https://www.google.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/api/v1/url/" + hash))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl(originalUrl));

        verify(urlService).getOriginalUrl(hash);
    }


}

