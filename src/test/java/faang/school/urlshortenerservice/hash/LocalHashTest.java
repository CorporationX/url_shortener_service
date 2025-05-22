package faang.school.urlshortenerservice.hash;

import faang.school.urlshortenerservice.properties.HashProperties;
import faang.school.urlshortenerservice.service.hash.HashService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LocalHashTest {

    private LocalHash localHash;

    @Mock
    private HashService hashService;

    @Mock
    private HashProperties hashProperties;


    private final HashProperties.Saving saving =  new HashProperties.Saving(10, Duration.ofDays(5));
    private final List<String> hashes = List.of("1", "2");

    @BeforeEach
    void setUp() {
        localHash = new LocalHash(hashService, hashProperties, Executors.newSingleThreadExecutor());
    }

    @Test
    void getHash() {
        when(hashProperties.getSaving()).thenReturn(saving);
        when(hashService.getHashes()).thenReturn(hashes);

        String hash = localHash.getHash();
        String hash2 = localHash.getHash();

        assertEquals(hashes.get(0), hash);
        assertEquals(hashes.get(1), hash2);
    }
}