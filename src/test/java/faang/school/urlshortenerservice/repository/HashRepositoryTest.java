package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static faang.school.urlshortenerservice.util.TestDataFactory.BATCH_SIZE;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("these tests will not be run")
class HashRepositoryTest extends AbstractionBaseTest {
    @Autowired
    private HashRepository repository;

    @Test
    void shouldReturnUniqueNumbersForGivenBatchSize() {
        // given - precondition
        // when - action
        var actualResult = repository.getUniqueNumbers(BATCH_SIZE);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).hasSize(BATCH_SIZE);
    }

    @Test
    void shouldDeleteAndReturnHashesWhenQuantityIsSpecified() {
        // given - precondition
        var expectedResult = of("abc123", "def456", "ghi789", "jkl012", "mno345", "pqr678", "stu901", "vwx234", "yz5678", "abc890");
        // when - action
        var actualResult = repository.getAndDeleteHashes(BATCH_SIZE);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).hasSize(BATCH_SIZE);
        assertThat(expectedResult).containsAll(actualResult);
    }
}