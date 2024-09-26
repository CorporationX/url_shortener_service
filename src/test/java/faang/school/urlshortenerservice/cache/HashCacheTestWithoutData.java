package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.TestBeans;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {HashCache.class, TestBeans.class})
@ActiveProfiles("withoutData")
public class HashCacheTestWithoutData {

    @Autowired
    private HashCache hashCache;
    @Autowired
    private HashGenerator hashGenerator;

    @Test
    void testGetHash() {
        List<String> hashes = List.of("hash", "hash", "hash");
        when(hashGenerator.getHashBatchAsync(any(Integer.class))).thenReturn(CompletableFuture.completedFuture(hashes));

        String result = hashCache.getHash();

        assertNotNull(result);
        verify(hashGenerator, times(1)).getHashBatchAsync(any(Integer.class));
    }
}