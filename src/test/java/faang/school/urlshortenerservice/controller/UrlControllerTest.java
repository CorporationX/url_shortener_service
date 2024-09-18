package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDtoRequest;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;
    @Mock
    private UrlService urlService;
    private MockMvc mockMvc;

    private UrlDtoRequest urlDtoRequest;
    private String json;

    @BeforeEach
     void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
        urlDtoRequest = UrlDtoRequest.builder()
                .url("http://example.com")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            json= objectMapper.writeValueAsString(urlDtoRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getShortUrlTest() throws Exception {
        when(urlService.getShortUrl(urlDtoRequest)).thenReturn(anyString());
        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
        verify(urlService, times(1)).getShortUrl(urlDtoRequest);
    }

    @Test
    void getOriginalTest() throws Exception {
         String hash = "abcABC";
        String expectedUrl = "http://example.com";
        when(urlService.getUrlFromHash(hash)).thenReturn(anyString());

//        mockMvc.perform(get("/url/{hash}", hash))
//                .andExpect(status().isFound())
//                .andExpect(redirectedUrl(expectedUrl));
    }
}
