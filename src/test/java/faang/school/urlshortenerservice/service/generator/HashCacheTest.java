package faang.school.urlshortenerservice.service.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashCacheTest {
  @Mock
  private HashGenerator generator;
  private HashCache cache;

  @BeforeEach
  void setUp() {
    int capacity = 10;
    int percent = 20;
    cache = new HashCache(generator, capacity, percent);
  }

  @Test
  void testGetHashWithEnoughHashes() {
    // given
    List<String> hashes = List.of("b", "c", "d", "e", "f", "g", "h", "i", "j", "k");
    when(generator.getHashes(Mockito.anyLong())).thenReturn(hashes);
    cache.init();
    // when
    String hashActual = cache.getHash();
    // then
    verify(generator, times(0)).getHashesAsync(Mockito.anyLong());
    Assertions.assertEquals(hashes.get(0), hashActual);
  }

  @Test
  void testGetHash() {
    // given
    List<String> hashes = List.of("b", "c");
    when(generator.getHashes(Mockito.anyLong())).thenReturn(hashes);
    cache.init();
    when(generator.getHashesAsync(Mockito.anyLong())).thenReturn(CompletableFuture.completedFuture(Mockito.anyList()));
    // when
    String hashActual = cache.getHash();
    // then
    verify(generator, times(1)).getHashesAsync(Mockito.anyLong());
    Assertions.assertEquals(hashes.get(0), hashActual);
  }

  @Test
  void testGetHashBounderValue() {
    // given
    List<String> hashes = List.of("b", "c", "d");
    when(generator.getHashes(Mockito.anyLong())).thenReturn(hashes);
    cache.init();
    // when
    String hashActual = cache.getHash();
    // then
    verify(generator, times(0)).getHashesAsync(Mockito.anyLong());
    Assertions.assertEquals(hashes.get(0), hashActual);
  }
}