package faang.school.urlshortenerservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static faang.school.urlshortenerservice.util.TestDataFactory.HASH;
import static faang.school.urlshortenerservice.util.TestDataFactory.SHORT_URL;
import static faang.school.urlshortenerservice.util.TestDataFactory.URL;
import static faang.school.urlshortenerservice.util.TestDataFactory.createUrlDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;
    @Mock
    private UrlService urlService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void init(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
    }
    @Test
    void givenValidUrlWhenSaveAndGetShortUrlThenReturnShortUrl() throws Exception {
        // given - precondition
        var urlDto = createUrlDto();
        var urlDtoJson = objectMapper.writeValueAsString(urlDto);

        when(urlService.saveAndGetShortUrl(any())).thenReturn(SHORT_URL);

        // when - action
        var response = mockMvc.perform(post("/api")
                .contentType("application/json")
                .content(urlDtoJson)
        );

        // then - verify the output
        response.andExpect(status().isCreated())
                .andExpect(content().string(SHORT_URL))
                .andDo(print());
    }

    @Test
    void getUrl() throws Exception {
        // given - precondition
        when(urlService.getUrl(HASH)).thenReturn(URL);

        // when - action
        var response = mockMvc.perform(get("/api/{hash}", HASH));

        // then - verify the output
        response.andExpect(status().isFound())
                .andExpect(redirectedUrl(URL))
                .andDo(print());
    }
}