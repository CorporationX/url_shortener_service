package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UrlService urlService;

    private final String testUrl = "https://example.com";
    private final String testHash = "abc123";
    private final String shortUrl = "http://localhost/" + testHash;
    private final String testUserId = "12345";

    @Test
    void givenValidUrl_whenSaveOriginalUrl_thenReturnCreatedWithLocation() throws Exception {
        given(urlService.saveOriginalUrl(testUrl)).willReturn(shortUrl);

        mockMvc.perform(post("/url")
                        .header("x-user-id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"" + testUrl + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", shortUrl));
    }

    @Test
    void givenInvalidUrl_whenSaveOriginalUrl_thenReturnBadRequest() throws Exception {
        mockMvc.perform(post("/url")
                        .header("x-user-id", testUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\":\"invalid-url\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidHash_whenRedirect_thenReturnFoundWithOriginalUrl() throws Exception {
        given(urlService.getOriginalUrl(testHash)).willReturn(testUrl);

        mockMvc.perform(get("/" + testHash)
                        .header("x-user-id", testUserId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", testUrl));
    }

    @Test
    void givenInvalidHash_whenRedirect_thenReturnNotFound() throws Exception {
        given(urlService.getOriginalUrl("invalid-hash"))
                .willThrow(new UrlNotFoundException("URL not found"));

        mockMvc.perform(get("/invalid-hash")
                        .header("x-user-id", testUserId))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenEmptyHash_whenRedirect_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/ ")
                        .header("x-user-id", testUserId))
                .andExpect(status().isBadRequest());
    }
}
