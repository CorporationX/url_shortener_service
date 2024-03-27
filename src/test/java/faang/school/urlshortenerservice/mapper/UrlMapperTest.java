package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.model.UrlCash;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UrlMapperTest {

    private UrlMapper urlMapper = new UrlMapperImpl();

    @Test
    void toUrlCash() {
        //Arrange
        String urlOrigin = "https://ya.ru";
        String hash = "hash";
        Url url = Url.builder().hash(hash).url(urlOrigin).build();

        //Act
        UrlCash result = urlMapper.toUrlCash(url);

        //Assert
        assertEquals(result.getHash(),hash);
        assertEquals(result.getUrl(),urlOrigin);
    }
}