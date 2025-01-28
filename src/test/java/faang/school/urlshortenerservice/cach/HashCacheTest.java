package faang.school.urlshortenerservice.cach;

import faang.school.urlshortenerservice.hash.HashGenerator;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private HashGenerator hashGenerator;

    @InjectMocks
    private HashCache hashCache;


    @Test
    void getHash() {
    }
}