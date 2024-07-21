package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.any;
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

    private MockMvc mockMvc;

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private final String expectedUrl = "http://example.com";
    private final String hash = "abc123";
    private final UrlCreateDto urlCreateDto = new UrlCreateDto();
    private final UrlDto expectedDto = new UrlDto();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        urlCreateDto.setUrl("http://example.com");
        expectedDto.setHash(hash);
        expectedDto.setUrl(expectedUrl);

        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void createUrlHash_returnsCreatedUrlDto() throws Exception {
        when(urlService.createUrlHash(any())).thenReturn(expectedDto);

        mockMvc.perform(post("/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(urlCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.hash").value("abc123"))
                .andExpect(jsonPath("$.url").value("http://example.com"));

        verify(urlService, times(1)).createUrlHash(any());
    }

    @Test
    void getUrlFromHash_redirectsToCorrectUrl() throws Exception {
        when(urlService.getUrlFromHash(hash)).thenReturn(expectedUrl);

        mockMvc.perform(get("/url/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedUrl));
    }
}