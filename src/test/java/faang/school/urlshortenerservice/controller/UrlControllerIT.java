package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.base.AbstractBaseContext;
import faang.school.urlshortenerservice.dto.request.UrlRequest;
import faang.school.urlshortenerservice.dto.response.UrlResponse;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlControllerIT extends AbstractBaseContext {

    @Autowired
    private UrlService urlService;

    @Test
    public void testShortenUrl() throws Exception {
        UrlRequest request = new UrlRequest("https://www.google.com/");

        String response = mockMvc.perform(post("/api/v1/url")
                        .header("x-user-id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UrlResponse urlResponse = objectMapper.readValue(response, UrlResponse.class);

        assertNotNull(urlResponse.shortUrl());
    }

    @Test
    public void testGetShortUrl() throws Exception {
        UrlRequest request = new UrlRequest("https://www.google.com/");
        UrlResponse urlResponse = urlService.shortenUrl(request);

        MvcResult result = mockMvc.perform(get("/api/v1/" + urlResponse.shortUrl())
                        .header("x-user-id", 1))
                .andExpect(status().isFound())
                .andReturn();

        String location = result.getResponse().getHeader("Location");

        assertEquals("https://www.google.com/", location);
    }
}
