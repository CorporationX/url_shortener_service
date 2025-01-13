package faang.school.urlshortenerservice.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HashRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private HashRepository hashRepository;

    @Test
    @DisplayName("Get list of unique numbers from sequence in DB: return first 5")
    void test_GetListOfUniqueNumbers_Success() {
        List<Long> result = hashRepository.getUniqueNumbers(5);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(1, result.get(0));
    }

    @Test
    @DisplayName("Get empty list if queries amount < 1")
    void test_GetListOfUniqueNumbers_Fail_EmptyList() {
        List<Long> result = hashRepository.getUniqueNumbers(0);
        System.out.println(result);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Get list of unique numbers from sequence in DB: return first 5, then next 3")
    void test_GetListOfUniqueNumbers_Success_DoubleRequest() {
        List<Long> resultOne = hashRepository.getUniqueNumbers(5);
        System.out.println(resultOne);

        assertNotNull(resultOne);
        assertEquals(5, resultOne.size());
        assertEquals(1, resultOne.get(0));

        List<Long> resultTwo = hashRepository.getUniqueNumbers(3);
        System.out.println(resultTwo);

        assertNotNull(resultTwo);
        assertEquals(3, resultTwo.size());
        assertEquals(6, resultTwo.get(0));
    }
}