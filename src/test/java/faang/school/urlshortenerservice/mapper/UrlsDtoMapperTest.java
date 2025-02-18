package faang.school.urlshortenerservice.mapper;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.model.Urls;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UrlsDtoMapperTest {
    private UrlsDtoMapper urlsDtoMapper = new UrlsDtoMapperImpl();

    @Test
    void toUrlDtoLongUrlSuccessTest() {

        String hashTest = "r34auT";
        String urlTest = "https://www.test-lond.com/url/v1/create-result-for-test";

        Urls urlsReference = Urls.builder()
                .url(urlTest)
                .hash(hashTest)
                .build();

        UrlDto urlDtoReference = UrlDto.builder()
                .url(urlTest)
                .build();

        UrlDto urlDtoResult = urlsDtoMapper.toUrlDtoLongUrl(urlsReference);

        assertEquals(urlDtoReference, urlDtoResult, "The method toUrlDtoLongUrl() is not correct.");
    }
}