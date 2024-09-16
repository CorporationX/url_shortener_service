package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlController controller;
    @Mock
    private UrlService service;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private final String hash = "aaaaaa";
    private final String longUrl = "http://localhost:8080/api/url/some_url";

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreateHash() throws Exception {
        // given
        String uri = "/api/url/";
        UrlDto requestDto = new UrlDto(longUrl);

        when(service.getShortUrl(requestDto)).thenReturn(hash);

        // then
        mockMvc.perform(post(uri)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void goToLongUrl() throws Exception {
        // given
        String uri = "/api/url/{hash}";

        when(service.getLongUrl(hash)).thenReturn(longUrl);

        // then
        mockMvc.perform(get(uri, hash))
                .andExpect(status().is(302))
                .andExpect(header().string("Location", longUrl) );
    }

}