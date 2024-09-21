package faang.school.urlshortenerservice.controller;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    private MockMvc mockMvc;
    @Mock
    private UrlService urlService;
    @InjectMocks
    private UrlController urlController;
    private ObjectWriter objectWriter;

    private String hash;
    private UrlDto urlDto;

    @BeforeEach
    void setUp() {
        hash = "testHash";
        urlDto = new UrlDto("https://www.anytesturl.com");
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
        objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @Test
    void testCreateUrl() throws Exception {
        when(urlService.createUrl(urlDto)).thenReturn(hash);
        String body = objectWriter.writeValueAsString(urlDto);
        mockMvc.perform(post("")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", is(hash)));
    }

    @Test
    void testGetUrl() throws Exception {
        when(urlService.getUrl(hash)).thenReturn(urlDto.getUrl());

        mockMvc.perform(get("/" + hash))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(urlDto.getUrl()));
    }
}