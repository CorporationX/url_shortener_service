package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Hash;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HashRepositoryTest extends AbstractDBTest {
  private HashRepository repository;

  @Autowired
  public HashRepositoryTest(HashRepository repository) {
    this.repository = repository;
  }

  @BeforeAll
  static void beforeAll() {
    POSTGRE_SQL_CONTAINER.start();
  }

  @AfterAll
  static void afterAll() {
    POSTGRE_SQL_CONTAINER.close();
  }

  @Test
  void testGetUniqueNumbers() {
    // given
    int range = 10;
    // when
    var actualNumbers = repository.getUniqueNumbers(range);
    // then
    assertNotNull(actualNumbers);
    assertEquals(range, actualNumbers.size());
  }

  @Test
  void testFindAndDelete() {
    // given
    int amount = 5;
    int countHashes = repository.findAll().size();
    // when
    var actualHashes = repository.findAndDelete(amount);
    // then
    assertEquals(amount, actualHashes.size());
    assertEquals(countHashes - amount, repository.findAll().size());
  }

  @Test
  void testSave() {
    // given
    Hash hash = Hash.builder().hash("k").build();
    // when
    Hash actualHash = repository.save(hash);
    // then
    assertEquals(8, actualHash.getId());
    assertEquals(hash.getHash(), actualHash.getHash());
  }

  @Test
  void testFindById() {
    // given
    Hash hashExp = new Hash(1L, "d");
    // when
    Optional<Hash> actualHash = repository.findById(1L);
    // then
    assertTrue(actualHash.isPresent());
    assertEquals(hashExp, actualHash.get());
  }
}