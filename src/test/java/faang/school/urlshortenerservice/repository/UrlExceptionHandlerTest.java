package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.ServiceTemplateApplication;
import faang.school.urlshortenerservice.exception.CustomException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ServiceTemplateApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UrlExceptionHandlerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHandleAllExceptions() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/triggerException", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal Server Error");
    }

    @Test
    public void testHandleValidationExceptions() {
        String requestBody = "{\"invalidField\":\"invalidValue\"}";
        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:" + port + "/validate", requestBody, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation error message");
    }

    @Test
    public void testHandleRuntimeExceptions() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/triggerRuntimeException", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).contains("Internal Server Error");
    }

    @Test
    public void testHandleCustomExceptions() {
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:" + port + "/triggerCustomException", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Custom exception message");
    }
}