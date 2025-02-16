package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlShortenerControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UrlShortenerService urlShortenerService;
    @InjectMocks
    private UrlShortenerController urlShortenerController;

    private int maxHashLength;
    private String urlController;
    private String hash;
    private String longUrl;
    private String shortUrl;
    private String urlName;

    @BeforeEach
    void setUp() {
        urlController = "/api/url_shortener/v1/url";
        urlName = "http://test-shortner-service.com/";
        longUrl = "http://www.test-urlshortener.com/long-url/v1/there-is-a-long-url-here";
        hash = "a1b1";
        shortUrl = urlName + hash;
        maxHashLength = 6;

        ReflectionTestUtils.setField(urlShortenerController, "urlName", urlName);
        ReflectionTestUtils.setField(urlShortenerController, "maxHashLength", maxHashLength);

        mockMvc = MockMvcBuilders.standaloneSetup(urlShortenerController).build();

        urlShortenerController.init();
    }

    @Test
    void getShortUrlSuccessTest() throws Exception {
        UrlDto longUrlDto = new UrlDto(longUrl);
        UrlDto expectedUrlDto = new UrlDto(urlName + "abc123");

        when(urlShortenerService.getShortUrl(anyString())).thenReturn(expectedUrlDto);

        mockMvc.perform(post(urlController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(longUrlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(expectedUrlDto.url()));
    }

    @Test
    void getLongUrlSuccessTest() throws Exception {

        UrlDto shortUrlDto = new UrlDto(shortUrl);
        UrlDto expectedUrlDto = new UrlDto(longUrl);

        when(urlShortenerService.getLongUrl(hash)).thenReturn(expectedUrlDto);

        mockMvc.perform(get(urlController)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(shortUrlDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url").value(expectedUrlDto.url()));
    }

    @Test
    void getLongUrlByHashSuccessTest() throws Exception {
        UrlDto shortUrlDto = new UrlDto(shortUrl);
        UrlDto expectedUrlDto = new UrlDto(longUrl);

        when(urlShortenerService.getLongUrl(hash)).thenReturn(expectedUrlDto);

        mockMvc.perform(get(urlController + "/" + hash))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));
    }
}