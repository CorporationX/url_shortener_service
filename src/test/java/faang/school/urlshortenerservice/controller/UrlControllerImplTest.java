package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.dto.HashDto;
import faang.school.urlshortenerservice.service.url.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(UrlControllerImpl.class)
class UrlControllerImplTest {

    private static final String ROOT_URL = "/api/v1/urls";
    protected static final String SAVE_URL = ROOT_URL + "/url";
    protected static final String GET_URL = ROOT_URL + "/{hash}";

    private static final String URL_IS_NOT_PRESENT =
            "Required request parameter 'url' for method parameter type String is not present";
    private static final String INVALID_HASH = "get.hash.hash: hash should not be null or blank";

    private final String url = "http://somesite.com";
    private final String hash = "iw31";
    private final HashDto hashDto = new HashDto(hash);

    @MockBean
    private UrlService urlService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void save() throws Exception {
        when(urlService.save(url)).thenReturn(hashDto);
        test(post(SAVE_URL + "?url=" + url), status().isOk(),
                content().string(objectMapper.writeValueAsString(hashDto)));
    }

    @Test
    void save_ShouldNotSaveWhenUrlIsNull() {
        test(post(SAVE_URL), status().isBadRequest(),
                jsonPath("$.message", is(URL_IS_NOT_PRESENT)));
    }

    @Test
    void get_ShouldGet() {
        when(urlService.get(hash)).thenReturn(url);
        test(get(GET_URL, hash), status().isFound(),
                header().string("Location", url));
    }

    @Test
    void get_ShouldNotGetWhenHashIsBlank() {
        test(get(GET_URL, " "), status().isBadRequest(),
                jsonPath("$.message", is(INVALID_HASH)));
    }

    private void test(MockHttpServletRequestBuilder endpoint,
                      ResultMatcher result, ResultMatcher responseContent) {
        try {
            mockMvc.perform(endpoint
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(result)
                    .andExpect(responseContent)
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}