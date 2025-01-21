package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.BaseContextIT;
import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
public class UrlControllerIT extends BaseContextIT {
    private static final String BASE_PATH = "/api/v1/urls";
    private static final String TEST_URL = "https://www.google.com/search?sca_esv=2e9d584eca4b7171&sxsrf=ADLYWIKyVWeQU8h7r5sglmzbhPLW571oow:1737252725068&q=%D0%BE%D1%87%D0%B5%D0%BD%D1%8C+%D1%81%D1%83%D0%BF%D0%B5%D1%80+%D0%BF%D1%83%D0%BF%D0%B5%D1%80+%D0%B4%D0%BB%D0%B8%D0%BD%D0%BD%D1%8B%D0%B9+%D0%B7%D0%B0%D0%BF%D1%80%D0%BE%D1%81&spell=1&sa=X&ved=2ahUKEwi4g8Dc2oCLAxXa0AIHHYikHpgQBSgAegQIChAB&cshid=1737252759802978&biw=1920&bih=934&dpr=1";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void generateShortUrlTest() throws Exception {
        UrlDto urlDto = new UrlDto(TEST_URL);
        String hash = mockMvc.perform(post(BASE_PATH)
                .header("x-user-id", 1)
                .content(objectMapper.writeValueAsString(urlDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();
        assertNotNull(hash);
    }

    @Test
    public void redirectToUrlTest() throws Exception {
        UrlDto urlDto = new UrlDto(TEST_URL);
        String hash = mockMvc.perform(post(BASE_PATH)
                .header("x-user-id", 1)
                .content(objectMapper.writeValueAsString(urlDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andReturn().getResponse().getContentAsString();

        String link = mockMvc.perform(get("/api/v1/urls/{shortUrl}", hash)
                .header("x-user-id", 1)
            )
            .andExpect(status().isFound())
            .andReturn().getResponse().getHeaders("Location").get(0);

        assertEquals(TEST_URL, link);
    }
}
