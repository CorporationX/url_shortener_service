package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.model.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlController.class)
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    private UrlDto urlDto;
    private UrlDto createdUrlDto;

    @BeforeEach
    public void setUp() {
        urlDto = new UrlDto();
        createdUrlDto = new UrlDto();
    }

    @Test
    public void testCreateUrl() throws Exception {
        urlDto.setOriginalUrl("https://www.example.com/fj2nfi2f?ff=ae82hvnsdf");
        createdUrlDto.setShortUrl("http://short.com/48fdj3");
        String json = objectMapper.writeValueAsString(urlDto);
        when(urlService.createUrlDto(urlDto)).thenReturn(createdUrlDto);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.shortUrl").value("http://short.com/48fdj3"));

        verify(urlService, times(1)).createUrlDto(urlDto);
    }

    @Test
    public void testCreateUrlWrongUrl() throws Exception {
        String json = objectMapper.writeValueAsString(urlDto);

        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType("application/json"));

        verify(urlService, never()).createUrlDto(any(UrlDto.class));
    }
}