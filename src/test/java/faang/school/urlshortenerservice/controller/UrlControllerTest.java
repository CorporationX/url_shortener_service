package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.exception.GlobalExceptionHandler;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import(GlobalExceptionHandler.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlController urlController;

    @MockBean
    private UrlService urlService;

    private String validLongUrl;
    private String jsonInvalidLongUrl;
    private String validHash;
    private String invalidHash;
    private String shortUrl;
    private LongUrlDto longUrlDto;
    private ShortUrlDto shortUrlDto;

    @BeforeEach
    void setUp() {
        validLongUrl = "https://www.shortmefasterplease.com/PLEASEPLEASEPLEASE";
        validHash = "HASHHH";
        shortUrl = "http://www.deema.com/" + validHash;
    }

    @Test
    void testCreateShortUrlSuccess() throws Exception {
        String jsonValidLongUrl = """
                {
                "longUrl":"https://www.shortmefasterplease.com/PLEASEPLEASEPLEASE"
                }""";

        longUrlDto = new LongUrlDto(validLongUrl);
        shortUrlDto = new ShortUrlDto(shortUrl);
        when(urlService.createShortUrl(longUrlDto)).thenReturn(shortUrlDto);

        mockMvc.perform(post("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValidLongUrl))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.shortUrl").value(shortUrl));
    }

    @Test
    void testCreateUrlFailed_WrongUrlFormat() throws Exception {
        jsonInvalidLongUrl = """
                {
                "longUrl":"htps://www.shortmefasterplease.com/PLEASEPLEASEPLEASE"
                }""";

        mockMvc.perform(post("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalidLongUrl))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid URL format")));
    }

    @Test
    void testCreateUrlFailed_EmptyUrl() throws Exception {
        jsonInvalidLongUrl = """
                {
                "longUrl":""
                }""";

        mockMvc.perform(post("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalidLongUrl))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid URL format")));
    }

    @Test
    void testGetLongUrlSuccess() throws Exception {
        when(urlService.getLongUrl(validHash)).thenReturn(validLongUrl);

        mockMvc.perform(get("/{hash}", validHash))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().stringValues(validLongUrl));
    }

    @Test
    void testGetLongUrlFailed_WrongHashSize() throws Exception {
        invalidHash = "hash";

        mockMvc.perform(get("/{hash}", invalidHash))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Hash must contain 6 chars.")));
    }

    @Test
    void testGetLongUrlFailed_WrongHashChars() throws Exception {
        invalidHash = "hash )";

        mockMvc.perform(get("/{hash}", invalidHash))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("0-9, a-z, A-Z chars only")));
    }

    @Test
    void testGetLongUrlFailed_BlankHash() throws Exception {
        invalidHash = "      ";

        mockMvc.perform(get("/{hash}", invalidHash))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Hash must not be empty")));
    }
}