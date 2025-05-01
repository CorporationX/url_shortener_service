package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@JdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(HashRepository.class)
public class HashRepositoryTest {

    @Autowired
    private HashRepository hashRepository;

    @Test
    void testGetUniqueNumbersReturnsCorrectNumberOfValues() {
        int requestedCount = 3;
        List<Long> result = hashRepository.getUniqueNumbers(requestedCount);

        Assertions.assertEquals(requestedCount, result.size());
    }
}
