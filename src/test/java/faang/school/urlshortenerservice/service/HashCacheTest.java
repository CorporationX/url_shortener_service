package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    @Mock
    private HashRepository hashRepository;

    @Spy
    private Base62Encoder base62Encoder;

    @InjectMocks
    private HashGenerator hashGenerator;
    private final HashCache cache = new HashCache(hashGenerator);

    @Test
    void testGetHashSuccess() {
        int capacity = 10000;
        List<Hash> hashes = new ArrayList<>(List.of(new Hash("hfs7gf"), new Hash("4iud4")));
        doReturn(hashes).when(hashRepository).getHashBatch(capacity);

        ArrayBlockingQueue<String> cacheHash = new ArrayBlockingQueue<>(capacity);
        ReflectionTestUtils.setField(cache, "hashes", cacheHash);
        ReflectionTestUtils.setField(cache, "FILLED_PERCENTAGE", 20);
        ReflectionTestUtils.setField(cache, "capacity", capacity);
        ReflectionTestUtils.setField(cache, "hashGenerator", hashGenerator);
        String hash = cache.getHash();
        verify(base62Encoder).encode(anyList());
        assertThat(hash).isEqualTo("hfs7gf");
    }
}