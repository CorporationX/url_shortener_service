package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlCreateDto;
import faang.school.urlshortenerservice.dto.UrlReadDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @InjectMocks
    private UrlController urlController;

    @Test
    public void testCreateShortUrlSuccessCase() throws Exception {
        UrlCreateDto createDto = new UrlCreateDto();
        createDto.setOriginalUrl("http://example.com");

        UrlReadDto urlReadDto = new UrlReadDto();
        urlReadDto.setHash("FFF");
        when(urlService.createShortUrl(createDto)).thenReturn(urlReadDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"http://example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateShortUrlNotValidated() throws Exception {
        UrlCreateDto createDto = new UrlCreateDto();
        createDto.setOriginalUrl("www.example.com");

        UrlReadDto urlReadDto = new UrlReadDto();
        urlReadDto.setHash("FFF");
        when(urlService.createShortUrl(createDto)).thenReturn(urlReadDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"originalUrl\":\"www.example.com\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testRedirectToLongUrl() throws Exception {
        String hash = "FFF";
        String originalUrl = "http://example.com";
        when(urlService.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(MockMvcRequestBuilders.get("/" + hash))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.header().string("Location", originalUrl));
    }

}
