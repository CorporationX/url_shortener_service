package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.LongUrl;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.service.cache.LocalCache;
import faang.school.urlshortenerservice.util.BaseContextTest;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SqlGroup({
        @Sql(scripts = "classpath:faang/school/urlshortenerservice/controller/scripts/insert-hashes.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:faang/school/urlshortenerservice/controller/scripts/insert-url.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
})
class UrlControllerTest extends BaseContextTest {
    private static final String API_URL = "/api/v1/url";

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private LocalCache localCache;

    @MockBean
    private LockProvider lockProvider;

    @BeforeEach
    void setUp() {
        localCache.initializeCache();
    }

    @Nested
    @DisplayName("POST /api/v1/url")
    class ShortUrlTests {
        @Test
        @DisplayName("Should return short URL and save to DB on success")
        @Transactional
        void getShortUrl_Success() throws Exception {
            LongUrl requestDto = new LongUrl("https://www.google.com/search?q=integration+testing");
            MvcResult result = mockMvc.perform(post(API_URL)
                            .header("x-user-id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andReturn();
            String responseBody = result.getResponse().getContentAsString();
            String[] parts = responseBody.split("/");
            String hash = parts[parts.length - 1];

            assertEquals("https://www.google.com/" + hash, responseBody);

            Optional<Url> savedUrl = urlRepository.findById(hash);
            assertTrue(savedUrl.isPresent());
            assertEquals(requestDto.getUrl(), savedUrl.get().getUrl());
        }

        @Test
        @DisplayName("POST /api/v1/url - Should correctly handle URLs with non-default ports")
        @Transactional
        void getShortUrl_WithCustomPortSuccess() throws Exception {
            LongUrl requestDto = new LongUrl("http://localhost:8080/my-app/resource");
            mockMvc.perform(post(API_URL)
                            .header("x-user-id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("http://localhost:8080/")));
        }

        @Test
        @DisplayName("POST /api/v1/url - Should return 400 Bad Request for invalid URL")
        @Transactional
        void getShortUrl_InvalidUrlFormat() throws Exception {
            LongUrl requestDto = new LongUrl("not-a-valid-url");
            mockMvc.perform(post(API_URL)
                            .header("x-user-id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value("url: URL must be valid"));
        }

        @Test
        @DisplayName("POST /api/v1/url - Should return 400 Bad Requet for blank URL")
        @Transactional
        void getShortUrl_BlankUrl() throws Exception {
            LongUrl requestDto = new LongUrl("");
            mockMvc.perform(post(API_URL)
                            .header("x-user-id", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(requestDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value(containsString("url: URL cannot be blank")));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/url/{hash}")
    class GetLongUrlTests {

        @Test
        @DisplayName("Should redirect to the long URL for a valid hash")
        @Transactional
        void getLongUrl_RedirectSuccess() throws Exception {
            mockMvc.perform(get(API_URL + "/hash")
                            .header("x-user-id", 1L))
                    .andExpect(status().isFound())
                    .andExpect(header().string("Location", "https://example.com/my-test-page"));
        }

        @Test
        @DisplayName("Should return 404 Not Found for a hash that does not exist")
        @Transactional
        void getLongUrl_NotFound() throws Exception {
            mockMvc.perform(get(API_URL + "/non")
                            .header("x-user-id", 1L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message")
                            .value("URL with hash 'non' not found"));
        }

        @Test
        @DisplayName("Should return 400 Bad Request for a hash > 6 chars")
        @Transactional
        void getLongUrl_BadRequest() throws Exception {
            mockMvc.perform(get(API_URL + "/nonExistentHash")
                            .header("x-user-id", 1L))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message")
                            .value("getLongUrl.hash: Hash must be 6 characters or less"));
        }
    }
}
