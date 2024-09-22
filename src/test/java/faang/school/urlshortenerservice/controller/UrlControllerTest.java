package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UrlService urlService;
    @Autowired
    private ObjectMapper objectMapper;

    private String longUrl = "http://someUrl.com";
    private String invalidUrl = "invalidUrl";
    private String hash = "hash";
    private String shortUrl = "http://host:port/" + hash;
    private String postUrlTemplate = "/v1/url";
    private String getUrlTemplate = "/v1/url/{hash}";

    @Test
    void testCreateShortUrl() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto(longUrl);
        UrlResponseDto responseDto = new UrlResponseDto(shortUrl);

        when(urlService.createShortUrl(longUrl)).thenReturn(responseDto);

        mockMvc.perform(post(postUrlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shortUrl").value(shortUrl));
    }

    @Test
    void testCreateShortUrlInvalidUrlFormat() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto(invalidUrl);

        mockMvc.perform(post(postUrlTemplate)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.url").value(postUrlTemplate))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.longUrl").value("Invalid format URL"));
    }

    @Test
    void testCreateShortUrlMissingUrl() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto("{}");

        mockMvc.perform(post(postUrlTemplate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.url").value(postUrlTemplate))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.fieldErrors.longUrl").value("Invalid format URL"));
    }

    @Test
    void testRedirectToLongUrl() throws Exception {
        when(urlService.getLongUrlByHash(hash)).thenReturn(longUrl);

        mockMvc.perform(get(getUrlTemplate, hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));
    }

    @Test
    void testRedirectToLongUrlHashNotFound() throws Exception {
        when(urlService.getLongUrlByHash(hash)).thenThrow(new UrlNotFoundException(hash));

        mockMvc.perform(get(getUrlTemplate, hash))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.url").value(postUrlTemplate + "/" + hash))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Url not found for hash: " + hash));
    }
}
