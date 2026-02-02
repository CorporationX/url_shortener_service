package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.config.ShortUrlProperties;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.UrlRequestDto;
import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UrlController.class)
@Import(ShortUrlProperties.class)
@TestPropertySource(properties = {
        "url.base.scheme=https",
        "url.base.domain=corporationx.com"
})
public class UrlControllerMvcTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserContext userContext;

    @MockBean
    UrlService service;

    @Captor
    ArgumentCaptor<UrlRequestDto> captor;

    @Test
    void shortenUrl_shouldPassCorrectDto_andReturnShortUrl() throws Exception {
        String hash = "g2jf45N";
        when(service.createShortUrl(any())).thenReturn(hash);

        mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                    {
                                      "url": "https://testing"
                                    }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.url")
                        .value("https://corporationx.com/" + hash));

        verify(service, times(1)).createShortUrl(captor.capture());
        assertThat(captor.getValue().url())
                .isEqualTo("https://testing");
    }

    @Test
    void shortenUrl_shouldReturn400_whenUrlRequestDtoInvalid() throws Exception {
        mockMvc.perform(post("/url")
                .header("x-user-id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                              "url": "invalid url"
                            }
                        """)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shortenUrl_shouldReturn500_whenLocalCacheEmpty() throws Exception {
        when(service.createShortUrl(any()))
                .thenThrow(new IllegalStateException("local cache is empty"));

        mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .content("""
                                    {
                                      "url": "https://testing"
                                    }
                                """))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @Test
    void redirectToLongLink_shouldPassCorrectHash_andReturn302() throws Exception {
        String hash = "N2h8i7";
        String originalUrl =
                "https://www.youtube.com/watch?v=dQw4w9WgXcQ&list=RDdQw4w9WgXcQ&start_radio=1";

        when(service.getOriginalUrl(hash)).thenReturn(originalUrl);

        mockMvc.perform(get("/" + hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isFound())
                .andExpect(header().string(HttpHeaders.LOCATION, originalUrl));

        verify(service, times(1)).getOriginalUrl(hash);
    }

    @Test
    void redirectToLongLink_shouldReturn404_whenHashInvalid() throws Exception {
        String invalidHash = "!@$%^&*";

        mockMvc.perform(get("/" + invalidHash)
                        .header("x-user-id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void redirectToLongLink_shouldReturn400_whenUrlNotFound() throws Exception {
        String hash = "rte34f";

        when(service.getOriginalUrl(hash))
                .thenThrow(new UrlNotFoundException("no url was found"));

        mockMvc.perform(get("/" + hash)
                        .header("x-user-id", "1"))
                .andExpect(status().isNotFound());
    }
}
