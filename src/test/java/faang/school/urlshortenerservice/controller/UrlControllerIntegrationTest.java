package faang.school.urlshortenerservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
//@AutoConfigureMockMvc
//@ExtendWith(SpringExtension.class)
//@ActiveProfiles("test")
//@Testcontainers
//public class UrlControllerIntegrationTest {
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Test
//    void testGetUrl() throws Exception {
//        String url = "https://example.com";
//        UrlDto urlDto = new UrlDto(url);
//
//        String response = mockMvc.perform(post("/url")
//                        .header("x-user-id", 2)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(urlDto)))
//                .andExpect(status().isCreated())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//
//        UrlDto shortenedUrlDto = objectMapper.readValue(response, UrlDto.class);
//        String hash = shortenedUrlDto.getUrl().split("/")[3];
//
//        mockMvc.perform(get("/" + hash)
//                        .header("x-user-id", 2))
//                .andExpect(status().isFound())
//                .andExpect(header().string(HttpHeaders.LOCATION, url));
//    }
//}

