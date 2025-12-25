package faang.school.urlshortenerservice.component.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.config.context.UserHeaderFilter;
import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.dto.CreateUrlRequestDto;
import faang.school.urlshortenerservice.exception.UrlExceptionHandler;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import({UrlExceptionHandler.class, UserHeaderFilter.class})
@DisplayName("UrlController validation, filter behavior & exception handling")
class UrlControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserContext userContext;

    @MockBean
    private UrlService urlService;

    private static final String USER_ID_HEADER = "x-user-id";
    private static final String USER_ID = "1";

    // ------------------- FILTER BEHAVIOR -------------------

    @Test
    @DisplayName("POST /url → 400 when x-user-id header is missing")
    void createUrl_missingUserHeader_returns400() throws Exception {
        CreateUrlRequestDto dto = new CreateUrlRequestDto("https://example.com");

        mvc.perform(post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("bad_request"));

        verifyNoInteractions(urlService);
    }

    @Test
    @DisplayName("GET /{hash} → 302 without x-user-id header (public redirect)")
    void redirect_withoutUserHeader_stillReturns302() throws Exception {
        when(urlService.getOriginalUrl("abc123"))
                .thenReturn("https://example.com");

        mvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));

        verify(urlService).getOriginalUrl("abc123");
    }

    // ------------------- VALIDATION -------------------

    @Test
    @DisplayName("POST /url → 400 + validation_error when URL is not valid HTTP URL")
    void createUrl_invalidUrl_returnsValidationErrorResponse() throws Exception {
        CreateUrlRequestDto dto = new CreateUrlRequestDto("not-a-url");

        mvc.perform(post("/url")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("validation_error"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.url").exists());

        verifyNoInteractions(urlService);
    }

    @Test
    @DisplayName("POST /url → 400 + validation_error when URL is blank")
    void createUrl_blankUrl_returnsValidationErrorResponse() throws Exception {
        CreateUrlRequestDto dto = new CreateUrlRequestDto(" ");

        mvc.perform(post("/url")
                        .header(USER_ID_HEADER, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("validation_error"))
                .andExpect(jsonPath("$.details.url").exists());

        verifyNoInteractions(urlService);
    }

    // ------------------- SUCCESS -------------------

    @Test
    @DisplayName("GET /{hash} → 302 + Location header when URL exists")
    void redirect_returns302_andLocationHeader() throws Exception {
        when(urlService.getOriginalUrl("abc123"))
                .thenReturn("https://example.com");

        mvc.perform(get("/abc123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com"));

        verify(urlService).getOriginalUrl("abc123");
    }

    // ------------------- EXCEPTIONS FROM SERVICE -------------------

    @Test
    @DisplayName("GET /{hash} → 404 + url_not_found when service throws UrlNotFoundException")
    void redirect_notFound_returns404_errorResponse() throws Exception {
        String hash = "Ab12Xy"; // 6 символов Base62

        when(urlService.getOriginalUrl(hash))
                .thenThrow(new UrlNotFoundException("Invalid hash: " + hash));

        mvc.perform(get("/" + hash))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("url_not_found"))
                .andExpect(jsonPath("$.message").value("Invalid hash: " + hash));

        verify(urlService).getOriginalUrl(hash);
    }

    @Test
    @DisplayName("GET /{hash} → 400 + bad_request when IllegalArgumentException occurs")
    void redirect_illegalArgument_returns400_errorResponse() throws Exception {
        when(urlService.getOriginalUrl("abc123"))
                .thenThrow(new IllegalArgumentException("Bad request"));

        mvc.perform(get("/abc123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad_request"))
                .andExpect(jsonPath("$.message").value("Bad request"));

        verify(urlService).getOriginalUrl("abc123");
    }

    @Test
    @DisplayName("GET /{hash} → 500 + internal_error on unexpected exception")
    void redirect_runtimeException_returns_errorResponse() throws Exception {
        when(urlService.getOriginalUrl("abc123"))
                .thenThrow(new RuntimeException("boom"));

        mvc.perform(get("/abc123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("internal_error"))
                .andExpect(jsonPath("$.message").value("Internal server error"));

        verify(urlService).getOriginalUrl("abc123");
    }
}
