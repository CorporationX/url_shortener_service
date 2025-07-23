package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlEncodeDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mockMvc;
    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void testRedirectByHash_ShouldReturnRedirectView() throws Exception {
        String hash = "abc123";
        String redirectUrl = "https://example.com/";
        when(urlService.getUrlByHash(hash)).thenReturn(redirectUrl);

        mockMvc.perform(get("/{hash}", hash))
            .andExpect(status().isFound())
            .andExpect(redirectedUrl(redirectUrl));

        verify(urlService).getUrlByHash(hash);
    }

    @Test
    void testEncodeUrl_ShouldReturnEncodedUrl() throws Exception {
        UrlEncodeDto urlDto = new UrlEncodeDto("https://example.com");

        String encodedUrl = "https://localhost/abc123";

        when(urlService.encodeUrl(any(UrlEncodeDto.class))).thenReturn(encodedUrl);

        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(urlDto)))
            .andExpect(status().isCreated())
            .andExpect(content().string(encodedUrl));

        verify(urlService).encodeUrl(any(UrlEncodeDto.class));
    }
}