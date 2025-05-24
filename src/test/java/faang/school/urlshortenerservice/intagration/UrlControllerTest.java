package faang.school.urlshortenerservice.intagration;

import faang.school.urlshortenerservice.BaseContextTest;
import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.properties.UrlTestProperties;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest extends BaseContextTest{
    @Autowired
    HashCache hashCache;

    @Autowired
    UserContext userContext;

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UrlTestProperties properties;

    private String fullUrl;

    @Test
    void shortenUrl_shouldReturn201AndHash() throws Exception {
        userContext.setUserId(1);
        String json = """
                {
                    "url": "https://youtube.com"
                }
                """;

        mockMvc.perform(post(properties.getShorten())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("x-user-id",1)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hash").exists());

    }

    @Test
    void get_shouldReturn302AndLocation() throws Exception {
        String hash = hashCache.getHash();
        fullUrl = properties.getRedirect()+ "?hash=" + hash;
        urlRepository.save(Url.builder().hash(hash).url("https://youtube.com").build());
        mockMvc.perform(get(fullUrl).header("x-user-id",1))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://youtube.com"));
    }

    @Test
    void get_shouldThrowEntityNotFoundException_whenHashDoesNotExist() throws Exception{
        String nonExistentHash = "nonexistent123";
        fullUrl = properties.getRedirect()+ "?hash=" + nonExistentHash;
        mockMvc.perform(get(fullUrl)
                        .header("x-user-id", 1))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Url by hash: " + nonExistentHash + " not found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.method").value("GET"))
                .andExpect(jsonPath("$.path").value(fullUrl));
    }
}