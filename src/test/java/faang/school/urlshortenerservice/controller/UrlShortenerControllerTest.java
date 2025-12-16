package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.CreateUrlRequestDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.handler.GlobalExceptionHandler;
import faang.school.urlshortenerservice.service.UrlShortenerService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlShortenerControllerTest {
    private final static String URL = "https://google.com";
    private final static String INCORRECT_URL = "om";
    private final static String HASH = "1S2D4H";

    private final String urlForCreateHash = URL;
    private final String incorrectUrlForCreateHash = INCORRECT_URL;
    private final String originalUrl = URL;
    private final String hash = HASH;
    private final String incorrectHash = HASH;
    private final CreateUrlRequestDto createUrlRequestDto = CreateUrlRequestDto
            .builder()
            .url(urlForCreateHash)
            .build();
    private final CreateUrlRequestDto incorrectCreateUrlRequestDto = CreateUrlRequestDto
            .builder()
            .url(incorrectUrlForCreateHash)
            .build();

    private final String createShortUrlJson = """
            {
                "url": "https://google.com"
            }
            """;

    private MockMvc mockMvc;
    private static Validator validator;

    @InjectMocks
    UrlShortenerController urlShortenerController;
    @Mock
    UrlShortenerService urlShortenerService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(urlShortenerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testSuccessfullyShortUrlGot() throws Exception {
        when(urlShortenerService.createShortUrl(createUrlRequestDto)).thenReturn(hash);
        mockMvc.perform(post("/url")
                        .contentType("application/json")
                        .content(createShortUrlJson))
                .andExpect(status().isCreated());
        verify(urlShortenerService, times(1)).createShortUrl(createUrlRequestDto);
    }

    @Test
    public void testSuccessfullyOriginalUrlGot() throws Exception {
        when(urlShortenerService.getOriginalUrl(hash)).thenReturn(originalUrl);
        mockMvc.perform(get("/{hash}", hash))
                .andExpect(status().is3xxRedirection());
        verify(urlShortenerService, times(1)).getOriginalUrl(hash);
    }

    @Test
    public void testFailShortUrlGotWhenUrlIncorrect() {
        Set<ConstraintViolation<CreateUrlRequestDto>> violations = validator.validate(incorrectCreateUrlRequestDto);

        assertThat(violations).isNotEmpty();
        assertThat(violations.iterator().next().getMessage()).contains("Invalid URL format");
    }

    @Test
    public void testFailOriginalUrlGotWhenHashNotFound() throws Exception {
        when(urlShortenerService.getOriginalUrl(hash))
                .thenThrow(new UrlNotFoundException("Hash not found"));
        mockMvc.perform(get("/{hash}", incorrectHash))
                .andExpect(status().isNotFound());
    }


    @Test
    void testFailExceptionWhenOriginalUrlGot() throws Exception {
        when(urlShortenerService.getOriginalUrl(incorrectHash))
                .thenThrow(new RuntimeException("Internal Server Error"));

        mockMvc.perform(get("/{hash}", incorrectHash)
                        .header("x-user-id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Internal Server Error"))
                .andExpect(jsonPath("$.path").value(String.format("/%s", incorrectHash)));
    }
}

