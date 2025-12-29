package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.RetryExecutor;
import faang.school.urlshortenerservice.exception.NoAvailableHashException;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Primary
@ConditionalOnProperty(name = "hash.cache.type", havingValue = "redis", matchIfMissing = true)
@RequiredArgsConstructor
public class HashCacheServiceRedis implements HashCacheService {

    private final StringRedisTemplate redisTemplate;
    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;
    private final MetricsService metricsService;
    private final RetryExecutor retryExecutor;
    
    private static final String HASH_POOL_KEY = "hash:pool";
    
    @Value("${hash.cache.fallback.max-concurrent:5}")
    private int maxConcurrentFallback;
    
    @Value("${hash.cache.max-size:1000}")
    private int maxCacheSize;
    
    private Semaphore fallbackSemaphore;

    @PostConstruct  
    public void init() {
        this.fallbackSemaphore = new Semaphore(maxConcurrentFallback);
        log.info("=== HashCacheServiceRedis is ACTIVE ===");
        log.info("HashCacheServiceRedis initialized with maxConcurrentFallback={}, maxCacheSize={}", 
                maxConcurrentFallback, maxCacheSize);
        log.info("Using Redis for hash pool storage. Key: {}", HASH_POOL_KEY);
        
        // Register gauge metric for cache size (legacy method - hash.cache.size)
        try {
            metricsService.registerHashCacheSize(this::size);
            log.info("Registered legacy gauge metric: hash.cache.size");
        } catch (Exception e) {
            log.warn("Failed to register legacy hash.cache.size metric: {}", e.getMessage());
        }
        
        try {
            log.info("Attempting to register hash.pool.size gauge...");
            
            metricsService.registerHashPoolSize(this::getPoolSizeAsDouble, "redis", HASH_POOL_KEY);
            
            log.info("Successfully registered gauge metric: hash.pool.size");

            double currentSize = getPoolSizeAsDouble();
            log.info("Current hash pool size: {}", currentSize);
            
        } catch (Exception e) {
            log.error("Failed to register hash.pool.size gauge", e);
        }

        fillRedisPool();
    }

    public void fillRedisPool() {
        try {
            int currentSize = size();
            if (currentSize >= maxCacheSize) {
                log.debug("Redis pool is already full (size={})", currentSize);
                return;
            }
            
            int needed = maxCacheSize - currentSize;
            log.info("Filling Redis pool: current size={}, target size={}, needed={}", 
                    currentSize, maxCacheSize, needed);

            List<String> hashes = hashRepository.getHashBatch(needed);
            
            if (hashes != null && !hashes.isEmpty()) {
                for (String hash : hashes) {
                    if (size() >= maxCacheSize) {
                        break;
                    }
                    retryExecutor.execute(() -> 
                        redisTemplate.opsForList().rightPush(HASH_POOL_KEY, hash)
                    );
                }
                
                log.info("Filled Redis pool with {} hashes. Current pool size: {}", 
                        hashes.size(), size());
            } else {
                log.warn("No hashes available in database to fill Redis pool");
            }
        } catch (Exception e) {
            log.error("Failed to fill Redis pool", e);
        }
    }

    @Override
    public String getHash() {
        int poolSizeBefore = size();
        String hash = retryExecutor.execute(() -> 
            redisTemplate.opsForList().leftPop(HASH_POOL_KEY)
        );
        int poolSizeAfter = size();
        
        if (hash != null && !hash.isEmpty()) {
            log.info("Retrieved hash from Redis pool: {} (pool size: {} -> {})", 
                    hash, poolSizeBefore, poolSizeAfter);
            metricsService.hashCacheHitCounter().increment();
            return hash;
        }

        log.warn("Redis pool is empty (size={}), falling back to database", poolSizeBefore);
        metricsService.hashCacheMissCounter().increment();
        return getHashFromDatabase();
    }

    @Override
    public void returnHash(String hash) {
        if (hash != null && !hash.isEmpty()) {
            retryExecutor.execute(() -> 
                redisTemplate.opsForList().rightPush(HASH_POOL_KEY, hash)
            );
            log.debug("Returned hash to Redis pool: {}", hash);
            metricsService.hashCacheReturnCounter().increment();
        }
    }

    @Override
    public int size() {
        Long size = retryExecutor.execute(() -> 
            redisTemplate.opsForList().size(HASH_POOL_KEY)
        );
        return size != null ? size.intValue() : 0;
    }

    /**
     * Returns pool size as Double for Gauge metric
     * Used by hash.pool.size gauge metric
     */
    private Double getPoolSizeAsDouble() {
        try {
            Long size = retryExecutor.execute(() -> 
                redisTemplate.opsForList().size(HASH_POOL_KEY)
            );
            return size != null ? size.doubleValue() : 0.0;
        } catch (Exception e) {
            log.warn("Failed to get pool size for metrics: {}", e.getMessage());
            return 0.0;
        }
    }

    private String getHashFromDatabase() {
        metricsService.hashCacheFallbackCounter().increment();
        try {
            if (!fallbackSemaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                log.warn("Too many concurrent fallback requests, rejecting");
                throw new NoAvailableHashException("Too many concurrent fallback requests");
            }
            
            try {
                List<String> hashes = retryExecutor.execute(() -> 
                    hashRepository.getHashBatchAtomic(1)
                );
                
                if (hashes != null && !hashes.isEmpty()) {
                    String hash = hashes.get(0);
                    log.info("Retrieved hash from database (fallback): {}", hash);
                    return hash;
                }

                log.warn("No hashes available in database, generating on-the-fly");
                metricsService.hashGenerationOnTheFlyCounter().increment();
                return generateHashImmediately();
                
            } finally {
                fallbackSemaphore.release();
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while waiting for fallback semaphore", e);
            throw new NoAvailableHashException("Interrupted while getting hash", e);
        }
    }

    private String generateHashImmediately() {
        metricsService.hashGenerationCounter().increment();
        try {
            List<Long> numbers = retryExecutor.execute(() -> 
                hashRepository.getUniqueNumbers(1)
            );
            
            if (numbers == null || numbers.isEmpty()) {
                throw new NoAvailableHashException("Sequence exhausted");
            }
            
            String hash = base62Encoder.encode(numbers.get(0));

            retryExecutor.execute(() -> hashRepository.saveAsUsed(hash));
            
            log.warn("Generated hash on-the-fly (last resort): {}", hash);
            return hash;
            
        } catch (NoAvailableHashException e) {
            throw e;
        } catch (Exception e) {
            log.error("CRITICAL: Cannot generate hash!", e);
            throw new NoAvailableHashException("Hash generation failed", e);
        }
    }
}

