package faang.school.urlshortenerservice.config.initiator;

import faang.school.urlshortenerservice.service.HashCache;
import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AppInitiatorTest {

    @InjectMocks
    private AppInitiator appInitiator;

    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private HashCache hashCache;

    @Test
    void run() {
        appInitiator.run();
        Mockito.verify(hashGenerator, Mockito.times(1))
                .generateBatchIfNeeded();
        Mockito.verify(hashCache, Mockito.times(1))
                .refill();
    }
}