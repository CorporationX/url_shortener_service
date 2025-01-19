package faang.school.urlshortenerservice.controller.url_shortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.UrlShortenerApplicationTests;
import faang.school.urlshortenerservice.dto.url.UrlDto;
import faang.school.urlshortenerservice.service.url_shortener.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class UrlShortenerControllerMockMvcTest extends UrlShortenerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    private static final String API_BASE_URL = "/api/v1/urls";

    @Test
    void shortenUrl_ShouldReturnShortenedUrl() throws Exception {
        UrlDto urlDto = new UrlDto("https://example.com");
        String shortenedUrl = "";
        Mockito.when(urlService.shortenUrl(Mockito.any(UrlDto.class))).thenReturn(shortenedUrl);

        mockMvc.perform(post(API_BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(urlDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(shortenedUrl));

        Mockito.verify(urlService, Mockito.times(1)).shortenUrl(Mockito.any(UrlDto.class));
    }

    @Test
    void redirectToOriginalUrl_ShouldSetRedirectionHeader() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";
        Mockito.when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get(API_BASE_URL + "/" + hash))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, originalUrl));

        Mockito.verify(urlService, Mockito.times(1)).getOriginalUrl(hash);
    }

    @Test
    void redirectToOriginalUrl_InvalidHash_ShouldReturnBadRequest() throws Exception {
        String invalidHash = "toolonghash";

        mockMvc.perform(get(API_BASE_URL + "/" + invalidHash))
                .andExpect(status().isBadRequest());
    }
}