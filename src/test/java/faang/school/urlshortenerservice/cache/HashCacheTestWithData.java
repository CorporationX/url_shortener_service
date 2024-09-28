package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.TestBeans;
import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {HashCache.class, TestBeans.class})
@ActiveProfiles("withData")
class HashCacheTestWithData {

    @Autowired
    private HashCache hashCache;
    @Autowired
    private HashGenerator hashGenerator;

    @Test
    void testGetHash() {
        String result = hashCache.getHash();

        assertNotNull(result);
        verify(hashGenerator, times(0)).getHashBatchAsync(any(Integer.class));
    }
}