package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
        urlDto = UrlDto.builder().url("https://www.google.com/search?q").build();

    }

    @Test
    void testGetUrlHash() throws Exception {
        String urlJson = objectMapper.writeValueAsString(urlDto);
        when(urlService.getUrlHash(ArgumentMatchers.any(UrlDto.class))).thenReturn("https://www.google.com/romats");

        mockMvc.perform(post("/api/v1/urls/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(urlJson))
                .andExpect(status().isOk());
    }

    @Test
    void testGetUrl() throws Exception {
        when(urlService.getOriginalUrl("adsads"))
                .thenReturn("https://www.google.com/romats");

        mockMvc.perform(get("/api/v1/urls/" + "adsads"))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("https://www.google.com/romats"));
    }

}
