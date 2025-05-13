package faang.school.urlshortenerservice.service;

import static org.junit.jupiter.api.Assertions.*;

/*@SpringBootTest
class HashCacheTest {

    @MockBean
    private HashRepository hashRepository;

    @MockBean
    private HashGenerator hashGenerator;

    @Autowired
    private HashCache hashCache;

    @Test
    void testInit() throws Exception {
        // Мокаем hashRepository.getHashBatch
        List<String> batch = Arrays.asList("000001", "000002", "000003");
        when(hashRepository.getHashBatch()).thenReturn(batch, Collections.emptyList());

        // Мокаем hashGenerator.generateBatch
        doAnswer(invocation -> {
            Thread.sleep(100);
            return null;
        }).when(hashGenerator).generateBatch();

        // Очищаем кэш
        Field cacheField = HashCache.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        Queue<String> cache = (Queue<String>) cacheField.get(hashCache);
        cache.clear();

        // Инициируем
        hashCache.init();

        // Даём время на завершение асинхронных операций (для теста)
        Thread.sleep(2000);

        // Проверяем
        assertEquals(3, cache.size(), "Cache should contain 3 hashes");
        verify(hashGenerator, times(10)).generateBatch();
        verify(hashRepository, atLeastOnce()).getHashBatch();
    }
}*/