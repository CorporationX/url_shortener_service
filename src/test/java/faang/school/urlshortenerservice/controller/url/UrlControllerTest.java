package faang.school.urlshortenerservice.controller.url;

import faang.school.urlshortenerservice.BaseContextTest;
import faang.school.urlshortenerservice.dto.url.RequestUrlBody;
import faang.school.urlshortenerservice.entity.url.Url;
import faang.school.urlshortenerservice.repository.url.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.url.UrlRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UrlControllerTest extends BaseContextTest {

    private static final String CORRECT_URL = "http://www.www.www";
    private static final String INCORRECT_URL = "www";
    private static final String HASH = "hash";
    private static final String USER_HEADER = "x-user-id";

    private static final long ID = 1L;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Autowired
    private UrlRepository urlRepository;

    private Url url;
    private RequestUrlBody requestUrlBody;

    @Test
    @Order(1)
    @DisplayName("When get /hash request with not exists hash should return exception")
    void whenHashNotExistsWhileRequestThenReturnException() throws Exception {
        url = Url.builder()
                .hash(HASH)
                .url(CORRECT_URL)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/urls/{hash}", HASH)
                                .header(USER_HEADER, ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Url with hash hash not found")));
    }

    @Test
    @Order(2)
    @DisplayName("When get /hash request with exists hash in db should redirect to full link")
    void whenHashIsExistsInDbWhileRequestThenExpectRedirection() throws Exception {
        url = Url.builder()
                .hash(HASH)
                .url(CORRECT_URL)
                .build();

        urlRepository.save(url);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/urls/{hash}", HASH)
                                .header(USER_HEADER, ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CORRECT_URL));
    }

    @Test
    @Order(3)
    @DisplayName("When get /hash request with exists hash in cache should redirect to full link")
    void whenHashIsExistsInCacheWhileRequestThenExpectRedirection() throws Exception {
        url = Url.builder()
                .hash(HASH)
                .url(CORRECT_URL)
                .build();

        urlRepository.delete(url);
        urlCacheRepository.save(url);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/v1/urls/{hash}", HASH)
                                .header(USER_HEADER, ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                ).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(CORRECT_URL));
    }

    @Test
    @DisplayName("When post request with correct body should return body")
    void whenBodyIsCorrectWhileRequestThenExpectResponse() throws Exception {
        requestUrlBody = RequestUrlBody.builder()
                .url(CORRECT_URL)
                .build();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/v1/urls")
                        .header(USER_HEADER, ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestUrlBody))
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("When post request with incorrect body should return exception")
    void whenBodyIsIncorrectWhileRequestThenExpectException() throws Exception {
        requestUrlBody = RequestUrlBody.builder()
                .url(INCORRECT_URL)
                .build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/v1/urls")
                                .header(USER_HEADER, ID)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(requestUrlBody))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Invalid URL format")));
    }
}