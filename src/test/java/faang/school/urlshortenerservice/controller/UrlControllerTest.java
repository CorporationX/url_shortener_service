package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest
@ContextConfiguration(classes = UrlController.class)
public class UrlControllerTest {

    private static final String USER_HEADER = "x-user-id";
    private static final String SHORTEN_URL_ENDPOINT = "/shortener";
    private static final String TEST_SHORT_HASH = "abc123";
    private static final String TEST_ORIGINAL_URL = "/www.yandex.ru";

    @MockBean

    private UrlService urlService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPositiveShortenUrl() throws Exception {
        UrlDto urlDto = UrlDto.builder()
                .url(TEST_ORIGINAL_URL)
                .build();
        when(urlService.shortenUrl(urlDto)).thenReturn(TEST_SHORT_HASH);

        mockMvc.perform(post(SHORTEN_URL_ENDPOINT)
                        .header(USER_HEADER, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(TEST_SHORT_HASH));
    }

    @Test
    void testPositiveRedirectToOriginalUrl() throws Exception {
        when(urlService.getOriginalUrl(TEST_SHORT_HASH)).thenReturn(TEST_ORIGINAL_URL);

        mockMvc.perform(get("/shortener/{hash}", TEST_SHORT_HASH)
                        .header(USER_HEADER, 1L))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", TEST_ORIGINAL_URL));
    }
}
