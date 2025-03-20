package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.dto.UrlResponseDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.handler.UrlExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@ContextConfiguration(classes = {
        UrlService.class,
        UrlController.class,
        UrlExceptionHandler.class})
public class UrlControllerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @Test
    public void testCreateShortUrl_Success() throws Exception {
        UrlRequestDto requestDto = UrlRequestDto.builder()
                .originalUrl("http://test.com")
                .build();

        UrlResponseDto responseDto = UrlResponseDto.builder()
                .shortUrl("short_url_test")
                .build();

        doReturn(responseDto).when(urlService).createShortUrl(any(), anyString());

        mockMvc.perform(post("/v1/url")
                        .header("x-user-id", "userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value("short_url_test"));
    }

    @Test
    public void testCreateShortUrl_InvalidUrl() throws Exception {
        UrlRequestDto requestDto = UrlRequestDto.builder()
                .originalUrl("invalid-url")
                .build();

        mockMvc.perform(post("/v1/url")
                        .header("x-user-id", "userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("originalUrl: Некорректный URL"));
    }

    @Test
    public void testCreateShortUrl_ServiceError() throws Exception {
        UrlRequestDto requestDto = UrlRequestDto.builder()
                .originalUrl("http://test.com")
                .build();

        doThrow(new RuntimeException("Internal Server Error"))
                .when(urlService).createShortUrl(any(), anyString());

        mockMvc.perform(post("/v1/url")
                        .header("x-user-id", "userId")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(OBJECT_MAPPER.writeValueAsString(requestDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Внутренняя ошибка сервера"));
    }

    @Test
    public void testGetOriginalUrl() throws Exception {
        String hash = "abc123";
        String originalUrl = "http://test.com";

        when(urlService.getUrlFromHash(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/v1/url/{hash}", hash))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }

    @Test
    public void testGetOriginalUrl_InvalidHash() throws Exception {
        String invalidHash = "1234!@";

        mockMvc.perform(get("/v1/url/{hash}", invalidHash))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Некорректный формат hash"));
    }

    @Test
    public void testGetOriginalUrl_UrlNotFound() throws Exception {
        when(urlService.getUrlFromHash("123"))
                .thenThrow(new UrlNotFoundException("URL не найден: 123"));

        mockMvc.perform(get("/v1/url/{hash}", "123"))
                .andExpect(status().isNotFound());
    }
}
