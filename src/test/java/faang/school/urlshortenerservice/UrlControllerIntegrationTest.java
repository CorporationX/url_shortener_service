package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.dto.UrlDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testRedirectShouldReturnFoundStatusAndLocationHeaderWhenHashExists() {
        String originalUrl = "https://example.com";
        String hash = "Ui";

        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(originalUrl);

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.setContentType(MediaType.APPLICATION_JSON);
        postHeaders.set("x-user-id", "12345");
        HttpEntity<UrlDto> postRequest = new HttpEntity<>(urlDto, postHeaders);

        restTemplate.postForEntity("/url", postRequest, String.class);

        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set("x-user-id", "12345");
        HttpEntity<Void> getRequest = new HttpEntity<>(getHeaders);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/{hash}",
                HttpMethod.GET,
                getRequest,
                Void.class,
                hash
        );

        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertNotNull(response.getHeaders().getLocation());
        assertEquals(originalUrl, response.getHeaders().getLocation().toString());
    }

    @Test
    public void testRedirectShouldReturnNotFoundWhenHashDoesNotExist() {
        String nonExistentHash = "nonExistentHash";

        HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set("x-user-id", "12345");
        HttpEntity<Void> getRequest = new HttpEntity<>(getHeaders);

        ResponseEntity<Void> response = restTemplate.exchange(
                "/{hash}",
                HttpMethod.GET,
                getRequest,
                Void.class,
                nonExistentHash
        );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateShortUrlShouldReturnCreatedShortUrlWhenUrlIsValid() {
        String originalUrl = "https://example.com";
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(originalUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-user-id", "12345");
        HttpEntity<UrlDto> request = new HttpEntity<>(urlDto, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/url",
                HttpMethod.POST,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testCreateShortUrlShouldReturnBadRequestWhenUrlIsInvalid() {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl("invalid-url");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-user-id", "12345");
        HttpEntity<UrlDto> request = new HttpEntity<>(urlDto, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("/url", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateShortUrlShouldReturnSameShortUrlForDuplicateUrls() {
        String originalUrl = "https://duplicate-example.com";
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(originalUrl);

        HttpHeaders postHeaders = new HttpHeaders();
        postHeaders.setContentType(MediaType.APPLICATION_JSON);
        postHeaders.set("x-user-id", "12345");
        HttpEntity<UrlDto> postRequest = new HttpEntity<>(urlDto, postHeaders);

        ResponseEntity<String> firstResponse = restTemplate.exchange(
                "/url",
                HttpMethod.POST,
                postRequest,
                String.class
        );
        String firstShortUrl = firstResponse.getBody();

        ResponseEntity<String> secondResponse = restTemplate.exchange(
                "/url",
                HttpMethod.POST,
                postRequest,
                String.class
        );

        assertEquals(HttpStatus.OK, secondResponse.getStatusCode());
        assertEquals(firstShortUrl, secondResponse.getBody());
    }
}
