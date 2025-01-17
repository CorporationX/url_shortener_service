package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateNewUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    @Spy
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testCreateShortUrl() throws Exception {
        CreateNewUrlDto createNewUrlDto = new CreateNewUrlDto();
        URL url = new URL("http://main-long.url/abc/adfaasdsdf");
        createNewUrlDto.setUrl(url);

        URL shortUrl = new URL("http://shortUrl.url/ssss");

        when(urlService.createNewShortUrl(any(URL.class))).thenReturn(shortUrl);

        mockMvc.perform(post("/api/v1/shorter/shortUrl/new")
                .header("x-user-id", 1)
                .content(objectMapper.writeValueAsString(createNewUrlDto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(shortUrl.toString()));

        verify(urlService, times(1)).createNewShortUrl(url);
    }

    @Test
    public void testGetUrl() throws Exception {
        String hash = "abcd";
        URL expectedUrl = new URL("https://example.com");

        when(urlService.getUrl(hash)).thenReturn(expectedUrl);

        mockMvc.perform(get("/api/v1/shorter/redirect/{hash}", hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(expectedUrl.toString()));

        verify(urlService, times(1)).getUrl(hash);
    }
}