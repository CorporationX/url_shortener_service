package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.AbstractIntegrationTest;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.repository.UrlCacheRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static faang.school.urlshortenerservice.TestUtils.CORRECT_URL_DTO;
import static faang.school.urlshortenerservice.TestUtils.USER_ID;
import static faang.school.urlshortenerservice.TestUtils.getHashFromShortLink;

public class CleanerSchedulerIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private HashRepository hashRepository;

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private UrlCacheRepository urlCacheRepository;

    @Test
    void clean_shouldBeCompletedSuccessfully() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shortener/url")
                        .header("x-user-id", USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CORRECT_URL_DTO)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String url = CORRECT_URL_DTO.url();
        String shortLink = result.getResponse().getContentAsString();
        String hash = getHashFromShortLink(shortLink);

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> hashRepository.get(hash));
        Assertions.assertEquals(url, urlRepository.get(hash));
        Assertions.assertEquals(url, urlCacheRepository.get(hash).orElseThrow());

        Thread.sleep(3000);

        Assertions.assertEquals(hash, hashRepository.get(hash));
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> urlRepository.get(hash));
        Assertions.assertTrue(urlCacheRepository.get(hash).isEmpty());
    }
}
