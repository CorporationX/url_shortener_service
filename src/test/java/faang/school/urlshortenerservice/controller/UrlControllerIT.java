package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.util.BaseContextTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(value = "/db/test_url_schema.sql")
public class UrlControllerIT extends BaseContextTest {

    @Test
    public void testGetUrlFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/100000"))
                .andExpect(status().isFound())
                .andReturn();

        String locationHeader = result.getResponse().getHeader("Location");

        assertEquals("https://faang-school.com/courses", locationHeader);
    }

    @Test
    public void testGetUrlNotFound() throws Exception {
        mockMvc.perform(get("/api/v1/200000"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("hash 200000 not found"));
    }

    @Test
    public void testSaveUrlIsOk() throws Exception {
        UrlDto urlDto = new UrlDto("https://faang-school.com/courses");

        mockMvc.perform(post("/api/v1/url")
                        .content(objectMapper.writeValueAsString(urlDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
    }
}
