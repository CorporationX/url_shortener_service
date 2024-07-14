package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlJpaRepository;
import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlControllerTest {
    @InjectMocks
    private UrlRepository urlRepository;
    @Mock
    private UrlJpaRepository jpaRepository;

    @Test
    void shortenUrl() {
        Url url = new Url();
        urlRepository.save(url);
        System.out.println();
    }

    @Test
    void getUrl() {
        System.out.println(2);
    }
}