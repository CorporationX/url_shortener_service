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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {

    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;

    private MockMvc mockMvc;
    private UrlDto urlDto;
    private final String hash = "qw";
    private UrlDto expectedDto;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        urlDto = UrlDto.builder().url("http://testlink").build();
        expectedDto = UrlDto.builder().url("http://" + hash).build();
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    public void testGetShortUrlWithGetting() throws Exception {
        when(urlService.getShortUrl(any())).thenReturn(expectedDto);

        mockMvc.perform(post("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.url").value(expectedDto.getUrl()));
    }

    @Test
    public void testGetLongUrlAndRedirect() throws Exception {
        when(urlService.getLongUrl(hash)).thenReturn(urlDto.getUrl());

        mockMvc.perform(get("/url/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().exists("Location"))
                .andExpect(redirectedUrl(urlDto.getUrl()));
    }
}
