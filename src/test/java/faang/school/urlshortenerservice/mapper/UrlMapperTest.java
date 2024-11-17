package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.ResponseUrlBody;
import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UrlMapperTest {

    @InjectMocks
    private UrlMapperImpl urlMapperImpl;

    @Test
    void testToResponseBody_successfulMapping(){

        Url url = Url.builder()
                .hash("abc")
                .url("https://docs.google.com/spredfadsheets/d/1GLfdCU9yUzE1JMZBmK7znxrRdfJzn-n4A5/edit?gid=0#gid=0")
                .build();

        String urlShortPrefix = "http://localhost:8080/";

        ResponseUrlBody response = urlMapperImpl.toResponseBody(url, urlShortPrefix);

        assertEquals(response.getShortUrl(), urlShortPrefix + url.getHash());
        assertEquals(response.getUrl(), url.getUrl());
    }
}
