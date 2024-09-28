package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.HashGenerator;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Configuration
public class TestBeans {

    @Value("${spring.task.array_blocking_queue_capacity}")
    private int capacity;
    @Value("${spring.task.fill_percent}")
    private double fillPercent;

    @Bean
    @Profile("withData")
    public HashGenerator hashGeneratorWithData() {
        HashGenerator hashGenerator = Mockito.mock(HashGenerator.class);
        List<String> hashes = IntStream.range(0, (int) (capacity * fillPercent) + 1)
                .mapToObj(i -> "hash")
                .toList();
        when(hashGenerator.getHashBatch(any(Integer.class))).thenReturn(hashes);
        return hashGenerator;
    }

    @Bean
    @Profile("withoutData")
    public HashGenerator hashGeneratorWithoutData() {
        HashGenerator hashGenerator = Mockito.mock(HashGenerator.class);
        when(hashGenerator.getHashBatch(any(Integer.class))).thenReturn(List.of("hash"));
        return hashGenerator;
    }
}
