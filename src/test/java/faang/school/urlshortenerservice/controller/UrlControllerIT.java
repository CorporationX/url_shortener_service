package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.repository.UrlRepository;
import faang.school.urlshortenerservice.util.BaseControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlControllerIT extends BaseControllerTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    public void testGetUrlFound() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/3EhmTD"))
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
    public void testSaveExistingUrl() throws Exception {
        int totalRowsBefore = urlRepository.findAll().size();
        UrlDto urlDto = new UrlDto("https://faang-school.com/courses");

        mockMvc.perform(post("/api/v1/url")
                        .content(objectMapper.writeValueAsString(urlDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        assertEquals(totalRowsBefore, urlRepository.findAll().size());
    }

    @Test
    public void testSaveNonExistingUrl() throws Exception {
        int totalRowsBefore = urlRepository.findAll().size();
        UrlDto urlDto = new UrlDto("https://faang-school.com");

        mockMvc.perform(post("/api/v1/url")
                        .content(objectMapper.writeValueAsString(urlDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        assertEquals(totalRowsBefore + 1, urlRepository.findAll().size());
    }
}
