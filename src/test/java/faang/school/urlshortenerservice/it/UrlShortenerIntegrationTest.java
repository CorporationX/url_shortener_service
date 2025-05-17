package faang.school.urlshortenerservice.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.UrlProperties;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.ContainersConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlShortenerIntegrationTest extends ContainersConfig {

    private static final String USER_PARAMETER = "x-user-id";

    private final int userId = 100;
    private final String firstUrl = "https://www.google.com";
    private final String hash = "hash1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlProperties urlProperties;

    @AfterEach
    void tearDown() {
        List<Url> urls = urlRepository.findAll();
        urls.forEach(url -> urlCacheRepository.evictUrlByHash(url.getHash()));
        urlRepository.deleteAll();
    }

    @Test
    void testNegativeCreateShortUrl() throws Exception {
        UrlDto urlDto = createUrlDto("unexpected-url");

        mockMvc.perform(post("/urls/url")
                        .header(USER_PARAMETER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPositiveCreateShortUrl() throws Exception {
        UrlDto urlDto = createUrlDto(firstUrl);

        mockMvc.perform(post("/urls/url")
                        .header(USER_PARAMETER, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(urlProperties.pattern())));
    }

    @Test
    void testNegativeRedirectToOriginalUrl() throws Exception {
        mockMvc.perform(get("/urls/{hash}", hash)
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPositiveRedirectToOriginalUrl() throws Exception {
        Url url = createUrl();
        urlRepository.save(url);

        mockMvc.perform(get("/urls/{hash}", url.getHash())
                        .header(USER_PARAMETER, userId))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", url.getUrl()));
    }

    private UrlDto createUrlDto(String url) {
        return UrlDto.builder()
                .longUrl(url)
                .build();
    }

    private Url createUrl() {
        return Url.builder()
                .url(firstUrl)
                .hash(hash)
                .build();
    }
}
