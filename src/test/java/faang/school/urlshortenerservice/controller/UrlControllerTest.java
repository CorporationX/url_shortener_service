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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UrlControllerTest {
     private MockMvc mockMvc;

     @Mock
     private UrlService urlService;

     @InjectMocks
     private UrlController urlController;

     @BeforeEach
     void setUp() {
          mockMvc = MockMvcBuilders.standaloneSetup(urlController).build();
     }


     @Test
     void testCreateUrl() throws Exception {
          UrlDto urlDto = UrlDto.builder().url("https://example.com").build();

          mockMvc.perform(post("/api/v1/shortener")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(urlDto)))
                  .andExpect(status().isCreated());

          verify(urlService).saveNewHash(any(UrlDto.class));
     }


     @Test
     void testGetUrl() throws Exception {
          when(urlService.findUrl("hz4")).thenReturn("https://example.com");

          mockMvc.perform(get("/api/v1/shortener/hz4"))
                  .andExpect(status().isFound())
                  .andExpect(header().string(HttpHeaders.LOCATION, "https://example.com"));

          verify(urlService).findUrl("hz4");
     }

     @Test
     void testCreateUrlInvalidUrl() throws Exception {
          UrlDto urlDto = UrlDto.builder().url("invalid-url").build();

          mockMvc.perform(post("/api/v1/shortener")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(urlDto)))
                  .andExpect(status().isBadRequest());

          verify(urlService, never()).saveNewHash(any(UrlDto.class));
     }

     @Test
     void testCreateUrlEmptyUrl() throws Exception {
          UrlDto urlDto = UrlDto.builder().url("").build();

          mockMvc.perform(post("/api/v1/shortener")
                          .contentType(MediaType.APPLICATION_JSON)
                          .content(new ObjectMapper().writeValueAsString(urlDto)))
                  .andExpect(status().isBadRequest());

          verify(urlService, never()).saveNewHash(any(UrlDto.class));
     }

     @Test
     void testGetUrlNotFound() throws Exception {
          when(urlService.findUrl("nonexistent")).thenReturn(null);

          mockMvc.perform(get("/api/v1/shortener/nonexistent"))
                  .andExpect(status().isNotFound());

          verify(urlService).findUrl("nonexistent");
     }

     @Test
     void testGetUrlEmptyHash() throws Exception {
          mockMvc.perform(get("/api/v1/shortener/"))
                  .andExpect(status().isMethodNotAllowed());

          verify(urlService, never()).findUrl(anyString());
     }
}
