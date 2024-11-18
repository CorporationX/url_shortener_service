package faang.school.urlshortenerservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.CacheCheckFilter;
import faang.school.urlshortenerservice.config.context.UserHeaderFilter;
import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.model.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.impl.UrlServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(controllers = UrlController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {CacheCheckFilter.class, UserHeaderFilter.class}))
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UrlServiceImpl urlService;

    @Test
    void testCreateShortUrl_Success() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto();
        requestDto.setOriginalUrl("https://www.example.com/aaaaaaa/aacccc/vvvv?abc=123");

        String shortUrl = "42abcd";

        when(urlService.createShortUrl(requestDto.getOriginalUrl())).thenReturn(shortUrl);

        mockMvc.perform(post("/url-shortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string(shortUrl));

        verify(urlService, times(1)).createShortUrl(requestDto.getOriginalUrl());
    }

    @Test
    void testCreateShortUrl_InvalidUrl() throws Exception {
        UrlRequestDto requestDto = new UrlRequestDto();
        requestDto.setOriginalUrl("invalid-url");

        mockMvc.perform(post("/url-shortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE));

        verify(urlService, never()).createShortUrl(anyString());
    }

    @Test
    void testRedirectUrl_Success() throws Exception {
        String hash = "test11";
        String originalUrl = "https://www.example.com/fghsfjaskfsafs/sdfsfasf";

        when(urlService.getUrlByHash(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/url-shortener/{hash}", hash))
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl(originalUrl));

        verify(urlService, times(1)).getUrlByHash(hash);
    }

    @Test
    void testRedirectUrl_HashNotFound() throws Exception {
        String hash = "invalidHash";

        when(urlService.getUrlByHash(hash)).thenThrow(new UrlNotFoundException("URL not found for hash: " + hash));

        mockMvc.perform(get("/url-shortener/{hash}", hash))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("URL not found for hash: " + hash));

        verify(urlService, times(1)).getUrlByHash(hash);
    }
}
