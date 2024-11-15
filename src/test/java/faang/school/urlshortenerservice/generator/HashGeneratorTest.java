package faang.school.urlshortenerservice.generator;

import faang.school.urlshortenerservice.encoder.Encoder;
import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@EnableAsync
@EnableScheduling
@TestPropertySource(properties = "spring.jpa.amount-hash=3")
class HashGeneratorTest extends AsyncConfigurerSupport {

    @Mock
    private HashRepository hashRepository;

    @Mock
    private Encoder encoder;

    @InjectMocks
    private HashGenerator hashGenerator;

    @Value("${spring.jpa.amount-hash}")
    private int amountHash;

    private List<Long> uniqueNumbers;
    private List<Hash> encodedHashes;

    @BeforeEach
    void setUp() {
        uniqueNumbers = Arrays.asList(1L, 2L, 3L);
        encodedHashes = Arrays.asList(
                Hash.builder().hash("000001").build(),
                Hash.builder().hash("000002").build(),
                Hash.builder().hash("000003").build()
        );

        when(hashRepository.getUniqueNumbers(amountHash)).thenReturn(uniqueNumbers);
        when(encoder.encode(uniqueNumbers)).thenReturn(encodedHashes);
    }

    @Test
    void testGenerateBatch() throws Exception {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> hashGenerator.generateBatch());

        future.get();

        verify(hashRepository).saveAll(encodedHashes);
        verify(hashRepository).getUniqueNumbers(amountHash);
        verify(encoder).encode(uniqueNumbers);
    }

    @Override
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.initialize();
        return executor;
    }
}