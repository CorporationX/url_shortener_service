package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.controller.UrlController;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    private MockMvc mockMvc;

    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;
    private ObjectMapper objectMapper;
    private UrlDto urlDto;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
        objectMapper = new ObjectMapper();
        urlDto = new UrlDto("https://faang-school.atlassian.net/jira/software/c/projects/BJS2/boards/159?selectedIssue=BJS2-55371");

    }

    @Test
    void testGenerateShortUrl() throws Exception {
        String shortUrl = "https://corp.x/v3f2";
        String originalUrlJson = objectMapper.writeValueAsString(urlDto);
        when(urlService.generateShortUrl(any(UrlDto.class))).thenReturn(shortUrl);

        mockMvc.perform(post("/api/v1/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(originalUrlJson))
                .andExpect(status().isOk())
                .andExpect(content().string(shortUrl));
    }

    @Test
    void testReturnFullUrl() throws Exception {
        when(urlService.returnFullUrl("v3f2")).thenReturn("https://faang-school.atlassian.net/jira/software/c/projects/BJS2");

        mockMvc.perform(get("/api/v1/urls?hash=v3f2"))
                .andExpect(status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("https://faang-school.atlassian.net/jira/software/c/projects/BJS2"));
    }
}
