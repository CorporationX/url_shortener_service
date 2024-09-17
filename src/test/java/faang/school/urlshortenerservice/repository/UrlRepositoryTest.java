package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("these tests will not be run")
class UrlRepositoryTest extends AbstractionBaseTest {
    @Autowired
    private UrlRepository repository;

    @Test
    void shouldDeleteUrlsOlderThanGivenTimeAndReturnHashes() {
        // given - precondition
        var yesterday = LocalDateTime.now().minusDays(1);
        var expectedResult = of("jkl012", "mno345", "pqr678", "stu901", "vwx234", "yz5678", "abc890");

        // when - action
        var actualResult = repository.deleteUrlsOlderThan(yesterday);

        // then - verify the output
        assertThat(actualResult).isNotNull();
        assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult);
    }
}