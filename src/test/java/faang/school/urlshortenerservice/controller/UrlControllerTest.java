package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.service.UrlService;
import faang.school.urlshortenerservice.validator.UrlValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    @Mock
    private UrlService urlService;
    @Mock
    private UrlValidator urlValidator;
    @InjectMocks
    private UrlController urlController;
    private MockMvc mockMvc;
    String hash = "abc123";
    String url = "https://example.com";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testAddUrl() throws Exception {
        when(urlService.add(url)).thenReturn(hash);

        mockMvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(url))
                .andExpect(status().isOk())
                .andExpect(content().string(hash));

        verify(urlValidator, times(1)).validate(url);
        verify(urlService, times(1)).add(url);
    }

    @Test
    void testGetUrl() throws Exception {
        when(urlService.get(hash)).thenReturn(url);

        mockMvc.perform(get("/" + hash))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(url));

        verify(urlService, times(1)).get(hash);
    }
}
