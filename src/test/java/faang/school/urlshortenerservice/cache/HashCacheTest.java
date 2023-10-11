package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashGenerator generator;
    @Mock
    private HashRepository repository;
    private HashCache hashCache;

    @BeforeEach
    public void setUp(){
        hashCache =new HashCache(generator, repository, 10, 20);
    }

    @Test
    void getHash() {
    }

    @Test
    void fillCache() {
    }

    @Test
    void addHash() {
    }
}