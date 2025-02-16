package faang.school.urlshortenerservice.cache;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalCacheTest {
    @Mock
    private HashGenerator hashGenerator;
    @Mock
    private LocalCacheRetry localCacheRetry;

    @InjectMocks
    private LocalCache localCache;

    private List<String> listTestHash;
    private Queue<String> testHashes;
    private int capacity;

    @BeforeEach
    void setUp() {
        capacity = 5;
        listTestHash = List.of("123", "234", "345", "qwe");
        ReflectionTestUtils.setField(localCache, "capacity", capacity);
        ReflectionTestUtils.setField(localCache, "fillPercent", 60);
    }

    @Test
    void initSuccessTest() {
        int expectedTestMinQueueSize = 3;

        when(hashGenerator.getHashes(capacity)).thenReturn(listTestHash);
        localCache.init();

        int testMinQueueSize = (int) ReflectionTestUtils.getField(localCache, "minQueueSize");
        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");

        assertEquals(expectedTestMinQueueSize, testMinQueueSize, " Check minQueueSize");

        listTestHash.forEach(s ->
                assertEquals(s, testHashes.poll(), " Check element in Queue<String>"));

        assertTrue(testHashes.isEmpty(), "Check Queue<String> should be empty");
    }

    @Test
    void initDoesNotTerminateOnExceptionSuccessTest() {
        when(hashGenerator.getHashes(capacity)).thenThrow(new IllegalStateException("Queue is full"));
        assertDoesNotThrow(() -> localCache.init());

        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");
        assertNotNull(testHashes, "Check Queue<String> should be NotNull");
        assertTrue(testHashes.isEmpty(), "Check Queue<String> should be empty");
    }

    @Test
    void getHashFillQueueSuccessTest() {
        listTestHash = List.of("123", "234", "345", "456", "567");
        when(hashGenerator.getHashes(capacity)).thenReturn(listTestHash);
        localCache.init();

        String testResult = localCache.getHash();

        assertEquals(listTestHash.get(0), testResult, "The result not equal: getHash() == listTestHash.get(0)");

        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");
        assertEquals(capacity - 1, testHashes.size(), "testHashes.size() !=  (capacity - 1)");
    }

    @Test
    void getHashNewItemsWereAddedToQueueSuccessTest() {
        listTestHash = List.of("123", "234");
        CompletableFuture<List<String>> listCompletableFuture =
                new CompletableFuture<>();
        listCompletableFuture.complete(List.of("323", "423", "523"));
        int neededNumberOfItems = 3;

        when(hashGenerator.getHashes(capacity)).thenReturn(listTestHash);
        when(hashGenerator.getHashesAsync(neededNumberOfItems)).thenReturn(listCompletableFuture);
        localCache.init();

        String testResult = localCache.getHash();

        assertEquals(listTestHash.get(0), testResult, "The result not equal: getHash() == listTestHash.get(0)");

        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");
        assertEquals(capacity - 1, testHashes.size(), "testHashes.size() !=  (capacity - 1)");
    }

    @Test
    void getHashQueueHas1ItemGenerationHashesFlagTrueSuccessTest() {
        listTestHash = List.of("123");
        AtomicBoolean fillingTest = new AtomicBoolean(true);
        ReflectionTestUtils.setField(localCache, "filling", fillingTest);

        when(hashGenerator.getHashes(capacity)).thenReturn(listTestHash);

        localCache.init();

        String testResult = localCache.getHash();
        assertEquals(listTestHash.get(0), testResult, "The result not equal: getHash() == listTestHash.get(0)");

        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");
        AtomicBoolean fillingTestResult = (AtomicBoolean) ReflectionTestUtils.getField(localCache, "filling");

        assertEquals(0, testHashes.size(), "testHashes.size() !=  0");
        assertTrue(fillingTestResult.get(), "AtomicBoolean filling should be True");
    }

    @Test
    void getHashQueueHas0ItemTrueSuccessTest() {
        String testHash = "a1b";
        listTestHash = List.of();
        AtomicBoolean fillingTest = new AtomicBoolean(true);
        ReflectionTestUtils.setField(localCache, "filling", fillingTest);

        when(hashGenerator.getHashes(capacity)).thenReturn(listTestHash);

        localCache.init();

        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");
        when(localCacheRetry.getCachedHash(testHashes)).thenReturn(testHash);

        String testResult = localCache.getHash();
        assertEquals(testHash, testResult, "The result not equal: getHash() == listTestHash.get(0)");

        testHashes = (Queue<String>) ReflectionTestUtils.getField(localCache, "hashes");
        AtomicBoolean fillingTestResult = (AtomicBoolean) ReflectionTestUtils.getField(localCache, "filling");

        assertEquals(0, testHashes.size(), "testHashes.size() !=  0");
        assertTrue(fillingTestResult.get(), "AtomicBoolean filling should be True");
    }
}