package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequest;
import faang.school.urlshortenerservice.service.interfaces.UrlService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {

    @Mock
    private UrlService urlService;

    @InjectMocks
    private UrlController urlController;

    private MockMvc mockMvc;
    private final long TEST_USER_ID = 123L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }

    @Test
    void createShortUrl_ValidRequest_ReturnsShortUrl() throws Exception {
        UrlRequest request = new UrlRequest("https://example.com");
        String expectedShortUrl = "abc123";

        when(urlService.createShortUrl(any(UrlRequest.class))).thenReturn(expectedShortUrl);

        mockMvc.perform(post("/api/v1/url")
                        .header("x-user-id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "url": "https://example.com"
                        }"""))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedShortUrl));
    }

    @Test
    void createShortUrl_InvalidUrl_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/url")
                        .header("x-user-id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "url": "invalid-url"
                        }"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShortUrl_MissingUserId_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "url": "https://example.com"
                        }"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirect_ValidHash_ReturnsRedirect() throws Exception {
        String hash = "abc123";
        String originalUrl = "https://example.com";

        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/api/v1/url/{hash}", hash)
                        .header("x-user-id", TEST_USER_ID))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(originalUrl));
    }

//    @Test
//    void redirect_InvalidHash_ReturnsNotFound() throws Exception {
//        String hash = "invalidHash";
//
//        when(urlService.getOriginalUrl(hash)).thenThrow(new NotFoundException(hash));
//
//        mockMvc.perform(get("/api/v1/url/{hash}", hash)
//                        .header("x-user-id", TEST_USER_ID))
//                .andExpect(status().isInternalServerError());
//    }

    @Test
    void createShortUrl_EmptyUrl_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/url")
                        .header("x-user-id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "url": ""
                    }"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShortUrl_NullUrl_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/url")
                        .header("x-user-id", TEST_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "url": null
                    }"""))
                .andExpect(status().isBadRequest());
    }
}