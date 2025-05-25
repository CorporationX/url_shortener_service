package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.BaseContextConfig;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlControllerTest extends BaseContextConfig {
    @Autowired
    private HashGenerator hashGenerator;

    @Test
    void givenUrl_whenCreateShortUrl_thenCreated() throws Exception {
        hashGenerator.checkAndGenerateHashesAsync();
        UrlDto urlDto = new UrlDto("https://yandex.ru/");

        MvcResult result = mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isCreated())
                .andReturn();

        String actualResult = result.getResponse().getContentAsString();

        String expectedUrl = "http://localhost:8080/short";
        assertTrue(actualResult.startsWith(expectedUrl));
    }

    @Test
    void givenBrokenUrl_whenCreateShortUrl_thenBadRequest() throws Exception {
        hashGenerator.checkAndGenerateHashesAsync();
        UrlDto urlDto = new UrlDto("h/yandex.ru/");

        mockMvc.perform(post("/url")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(urlDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenBrokenHash_whenGetUrl_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/nkajja")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Sql(scripts = "/add-url.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    public void givenHash_whenGetUrl_thenReturnNotFound() throws Exception {
        mockMvc.perform(get("/88jja")
                        .header("x-user-id", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is3xxRedirection());
    }
}
