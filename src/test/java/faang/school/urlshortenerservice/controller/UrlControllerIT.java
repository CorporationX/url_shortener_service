package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class UrlControllerIT extends BaseContextTest {
    private static final String TEST_URL = "https://faang-school.com/courses";

    private UrlDto urlDto;

    @BeforeEach
    public void setup() {
        urlDto = new UrlDto(TEST_URL);
    }

    @Test
    public void testGenerateShortUrl() throws Exception {
        String shortLink = mockMvc.perform(post("/api/v1/url")
                .header("x-user-id", 1)
                .content(objectMapper.writeValueAsString(urlDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        assertNotNull(shortLink);
    }

    @Test
    public void getUrl() throws Exception {
        String shortLink = mockMvc.perform(post("/api/v1/url")
                .header("x-user-id", 1)
                .content(objectMapper.writeValueAsString(urlDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andReturn().getResponse().getContentAsString();

        String link = mockMvc.perform(get("/api/v1/url/{shortUrl}", shortLink)
                .header("x-user-id", 1)
            )
            .andExpect(status().isFound())
            .andReturn().getResponse().getHeaders("Location").get(0);

        assertEquals(TEST_URL, link);
    }
}
