package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GeneratorSchedulerTest {

    @Mock
    private HashGenerator hashGenerator;
    @InjectMocks
    private GeneratorScheduler generatorScheduler;

    @Test
    void testGenerateUniqueNumbers() {
        generatorScheduler.generateUniqueNumbers();
        Mockito.verify(hashGenerator).generateBatch();
    }
}
