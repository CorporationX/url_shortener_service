package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.handler.UrlExceptionHandler;
import faang.school.urlshortenerservice.handler.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(UrlController.class)
@ContextConfiguration(classes = {UrlController.class, UrlExceptionHandler.class})
class UrlControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlService urlService;

    @InjectMocks
    UrlController urlController;

    UrlRequestDto urlRequestDto = new UrlRequestDto("http://www.any");
    String resultUrl = "http://www.any";
    String unknownUrl = "http://unknown.url";
    String hash = "abc123";

    @Nested
    class CreateShortUrl {
        @Test
        void successful() throws Exception {
            when(urlService.createOrResolveUrl(urlRequestDto.getUrl())).thenReturn(resultUrl);

            mockMvc.perform(post("/url")
                            .contentType("application/json")
                            .content("{\"url\":\"http://www.any\"}"))
                    .andExpect(status().isOk());

            verify(urlService, times(1)).createOrResolveUrl(urlRequestDto.getUrl());
        }

        @Test
        void urlNotFoundException() throws Exception {
            when(urlService.createOrResolveUrl(unknownUrl))
                    .thenThrow(new UrlNotFoundException("URL not found"));

            mockMvc.perform(post("/url")
                            .contentType("application/json")
                            .content("{\"url\": \"http://unknown.url\"}"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType("application/json"))
                    .andExpect(jsonPath("$.message").value("URL not found"))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.error").value("Not Found"));

            verify(urlService).createOrResolveUrl(unknownUrl);
        }

        @Test
        void redirect() throws Exception {
            when(urlService.resolveUrl(hash))
                    .thenReturn(resultUrl);

            mockMvc.perform(get("/url/" + hash))
                    .andExpect(status().isFound())
                    .andExpect(header().string("Location", resultUrl));

            verify(urlService).resolveUrl(hash);
        }
    }

    @Nested
    class Redirect {
        @Test
        void successful() throws Exception {
            when(urlService.resolveUrl(hash)).thenReturn(resultUrl);

            mockMvc.perform(get("/url/" + hash))
                    .andExpect(status().isFound())
                    .andExpect(header().string("Location", resultUrl));

            verify(urlService, times(1)).resolveUrl(hash);
        }
    }

}