package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.OriginalUrlDto;
import faang.school.urlshortenerservice.dto.ShortUrlDto;
import faang.school.urlshortenerservice.entity.Hash;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
    @InjectMocks
    private UrlController urlController;

    @Mock
    private UrlService urlService;

    private MockMvc mockMvc;

    private String domainAddress;

    private OriginalUrlDto originalUrl;
    private String fullUrl = "https://faangschol.com/abcxyz123456789";

    private ShortUrlDto shortUrlDto;

    private Hash hashEntity;
    private String hash = "12345";


    @BeforeEach
    void init() {
        domainAddress = "null";
        originalUrl = new OriginalUrlDto(fullUrl);
        hashEntity = new Hash("12345");
        shortUrlDto = new ShortUrlDto(domainAddress + hashEntity.getHash());
        mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();

    }


    @Test
    public void getShortUrlSuccessFullWithoutMockMVCTest() {
        when(urlService.saveUrlAssociation(originalUrl)).thenReturn(hashEntity);

        ShortUrlDto result = urlController.getShortUrl(originalUrl);

        assertEquals(shortUrlDto.getShortUrl(), result.getShortUrl());

    }


    @Test
    public void getShortUrlUnsuccessFullTest() throws Exception {
        String invalidUrlJson = "{ \"url\": \" \" }";

        mockMvc.perform(post("/urlShortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUrlJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void getShortUrlSuccessFullTest() throws Exception {
        String validUrlJson = "{ \"url\": \"https://example.com\" }";
        String expectedShortUrlJson = "{ \"shortUrl\": \"null12345\" }";

        when(urlService.saveUrlAssociation(any())).thenReturn(hashEntity);

        mockMvc.perform(post("/urlShortener")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validUrlJson))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedShortUrlJson));
    }

    @Test
    public void redirectSuccessTest() throws Exception {
        String redirectUrl = "https://example.com";

        when(urlService.getUrlByHash(hash)).thenReturn(redirectUrl);

        mockMvc.perform(get("/urlShortener/{hash}", hash))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(redirectUrl));
    }

    @Test
    public void redirectNotFoundTest() throws Exception {
        when(urlService.getUrlByHash(hash)).thenReturn(null);

        mockMvc.perform(get("/urlShortener/{hash}", hash))
                .andExpect(status().isNotFound());
    }

}
