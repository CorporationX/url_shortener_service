package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(UrlController.class)
public class UrlControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UrlService service;

    @MockBean
    UserContext context;

    @Autowired
    ObjectMapper objectMapper;

    private final String userId = "123";

    @Test
    public void getShortLink() throws Exception {
        String originalUrl = "https://www.google.refer-friend??/gg9201?";
        String shortUrl = "https://www.shortener/abc";
        UrlRequestDto urlRequestDto = new UrlRequestDto(originalUrl);
        String requestBody = objectMapper.writeValueAsString(urlRequestDto);


        when(service.getShortUrlLink(originalUrl)).thenReturn(shortUrl);

        mockMvc.perform(post("/url/", originalUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .header("x-user-id", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));
    }

    @Test
    public void redirect() throws Exception {
        String originalUrl = "https://www.google.refer-friend??/gg9201?";
        String hash = "abc";

        when(service.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/url/{hash}", hash)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id", userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", originalUrl));
    }


}
