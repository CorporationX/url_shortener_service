package faang.school.urlshortenerservice.service.hash.util;

import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static faang.school.urlshortenerservice.test.utils.TestData.HASHES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
    private static final int HASHES_MAX = 10;
    private static final int HASHES_MIN = 2;

    @Mock
    private HashService hashService;

    @Mock
    private HashGenerator hashGenerator;

    @Mock
    private Executor executor;

    @InjectMocks
    private HashCache hashCache;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(hashCache, "hashesMax", HASHES_MAX);
        ReflectionTestUtils.setField(hashCache, "hashesMin", HASHES_MIN);
    }

    @SuppressWarnings("unchecked")
    @Test
    void testLoadHashes_successful() {
        when(hashService.findAllByPackSize(HASHES_MAX)).thenReturn(HASHES);

        hashCache.loadHashes();

        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assert hashes != null;
        assertThat(hashes.size()).isEqualTo(HASHES.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    void testGetHash_hashesEnough() {
        Queue<String> hashes = new ConcurrentLinkedDeque<>(HASHES);
        ReflectionTestUtils.setField(hashCache, "hashes", hashes);

        assertThat(hashCache.getHash())
                .isEqualTo(HASHES.get(0));

        verify(executor, never()).execute(any(Runnable.class));

        Queue<String> hashesResult = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assert hashesResult != null;
        assertThat(hashesResult.size())
                .isEqualTo(HASHES.size() -1);
    }

    @Test
    void testGetHash_hashesNotEnough_isUpdatingTrue() {
        ReflectionTestUtils.setField(hashCache, "isUpdating", new AtomicBoolean(true));

        hashCache.getHash();

        verify(executor, never()).execute(any(Runnable.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    void testUpdateHashes_successful() {
        when(hashService.findAllByPackSize(HASHES_MAX)).thenReturn(HASHES);

        ReflectionTestUtils.invokeMethod(hashCache, "updateHashes");

        verify(hashGenerator).generate();

        AtomicBoolean isUpdating = (AtomicBoolean) ReflectionTestUtils.getField(hashCache, "isUpdating");
        assert isUpdating != null;
        assertThat(isUpdating.get()).isFalse();

        Queue<String> hashes = (Queue<String>) ReflectionTestUtils.getField(hashCache, "hashes");
        assert hashes != null;
        assertThat(hashes.size()).isEqualTo(HASHES.size());
    }
}