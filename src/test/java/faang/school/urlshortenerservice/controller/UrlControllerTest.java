package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.LongUrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlController controller;
    @Mock
    private UrlService service;
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;
    private String testHash;
    private String longUrl;

    @BeforeEach
    void setUp() {
        // Arrange
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        testHash = "Af2";
        longUrl = "http://localhost:8080/api/url/test";
    }

    @Test
    void testCreateShortUrl() throws Exception {
        // Arrange
        LongUrlDto dto = new LongUrlDto(longUrl);
        //Act
        when(service.createShortUrl(Mockito.any())).thenReturn(testHash);
        //Assert
        MvcResult result = mockMvc.perform(post("/api/url")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertEquals(content, testHash);
    }
}