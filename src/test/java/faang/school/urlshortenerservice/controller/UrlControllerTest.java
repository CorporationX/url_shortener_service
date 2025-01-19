package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
     private MockMvc mockMvc;

     @Mock
     private UrlService urlService;

     @InjectMocks
     private UrlController urlController;

     @BeforeEach
     void setUp() throws Exception {
          mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
          Field baseUrlField = UrlController.class.getDeclaredField("baseUrl");
          baseUrlField.setAccessible(true);
          baseUrlField.set(urlController, "https://example.com");
     }


     @Test
     void testCreateUrl() throws Exception {
          UrlDto urlDto = new UrlDto("https://example.com");

          when(urlService.saveNewHash(any(UrlDto.class))).thenReturn("short123");

          mockMvc.perform(post("/url")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(urlDto)))
                  .andExpect(status().isCreated())
                  .andExpect(result -> {
                       String response = result.getResponse().getContentAsString();
                       response.contains("short123");
                  });
     }

     @Test
     void testGetUrl() throws Exception {
          when(urlService.findUrl("short123")).thenReturn("https://example.com");

          mockMvc.perform(get("/short123"))
                  .andExpect(status().isFound())
                  .andExpect(header().string(HttpHeaders.LOCATION, "https://example.com"));

          verify(urlService).findUrl("short123");
     }

     @Test
     void testCreateUrlInvalidUrl() throws Exception {
          UrlDto urlDto = new UrlDto("invalid-url");

          mockMvc.perform(post("/url")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(urlDto)))
                  .andExpect(status().isBadRequest());

          verify(urlService, never()).saveNewHash(any(UrlDto.class));
     }

     @Test
     void testCreateUrlEmptyUrl() throws Exception {
          UrlDto urlDto = new UrlDto("");

          mockMvc.perform(post("/url")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(urlDto)))
                  .andExpect(status().isBadRequest());

          verify(urlService, never()).saveNewHash(any(UrlDto.class));
     }

     @Test
     void testGetUrlNotFound() throws Exception {
          when(urlService.findUrl("nonexistent")).thenReturn(null);

          mockMvc.perform(get("/nonexistent"))
                  .andExpect(status().isFound());

          verify(urlService).findUrl("nonexistent");
     }

     @Test
     void testGetUrlEmptyHash() throws Exception {
          mockMvc.perform(get(""))
                  .andExpect(status().isNotFound());

          verify(urlService, never()).findUrl(anyString());
     }

     @Test
     void testGetUrl_WhenHashNotFound_ShouldRedirectToErrorPage() throws Exception {
          String hash = "nonexistentHash";

          when(urlService.findUrl(hash)).thenReturn(null);

          mockMvc.perform(get("/" + hash))
                  .andExpect(status().isFound())
                  .andExpect(redirectedUrl("/error-page"));
     }

     @Test
     void testCreateUrl_WhenInvalidUrl_ShouldReturnBadRequest() throws Exception {
          UrlDto invalidUrlDto = new UrlDto("");

          mockMvc.perform(post("/url")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(invalidUrlDto)))
                  .andExpect(status().isBadRequest());
     }

     @Test
     void testResolveUrl_SuccessfulRedirect() throws Exception {
          String shortHash = "shortHash";
          String originalUrl = "https://example.com/original";

          when(urlService.findUrl("https://example.com/short")).thenReturn(shortHash);
          when(urlService.findUrl(shortHash)).thenReturn(originalUrl);

          UrlDto urlDto = new UrlDto("https://example.com/short");
          String requestBody = new ObjectMapper().writeValueAsString(urlDto);

          mockMvc.perform(post("/resolve")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(requestBody))
                  .andExpect(status().isFound())
                  .andExpect(redirectedUrl(originalUrl));
     }


     @Test
     void testResolveUrl_NotFound() throws Exception {
          when(urlService.findUrl(anyString())).thenReturn(null);

          UrlDto urlDto = new UrlDto("https://example.com/short");
          String requestBody = new ObjectMapper().writeValueAsString(urlDto);

          mockMvc.perform(post("/resolve")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(requestBody))
                  .andExpect(status().isFound())
                  .andExpect(redirectedUrl("/error-page"));
     }

     @Test
     void testResolveUrl_InvalidRequestBody() throws Exception {
          UrlDto urlDto = new UrlDto("");
          String requestBody = new ObjectMapper().writeValueAsString(urlDto);

          mockMvc.perform(post("/resolve")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(requestBody))
                  .andExpect(status().isBadRequest());
     }
}
