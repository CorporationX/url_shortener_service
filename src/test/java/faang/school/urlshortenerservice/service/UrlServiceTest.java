package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UrlServiceTest {
    @Mock
    private UrlRepository urlRepository;
    @InjectMocks
    private UrlServiceImpl urlService;

    List<String> stringList;

    @BeforeEach
    public void setUp(){
        stringList = List.of("12klsd", "poas92");
    }
}
