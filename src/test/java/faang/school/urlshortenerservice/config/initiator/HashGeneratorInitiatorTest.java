package faang.school.urlshortenerservice.config.initiator;

import faang.school.urlshortenerservice.service.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashGeneratorInitiatorTest {

    @InjectMocks
    private HashGeneratorInitiator hashGeneratorInitiator;

    @Mock
    private HashGenerator hashGenerator;

    @Test
    void run() {
        hashGeneratorInitiator.run();
        Mockito.verify(hashGenerator, Mockito.times(1))
                .generateBatchIfNeeded();
    }
}