package faang.school.urlshortenerservice.generator;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class Base62Encoder {
    @Value("${hash.hash.cache.solWord}")
    private String solWord;
    private int solWordLength;

    @PostConstruct
    public void init(){
        log.info("Start PostConstruct init Base62Encoder");
        solWordLength = solWord.length();
    }

    public List<String> encodeList(List<Long> numbers) {
        log.info("Start hashes generation. From long to hash: {}", numbers == null ? 0 : numbers.size());
        try {
            return numbers.stream().map(this::encodeNumber).toList();
        } catch (NullPointerException e){
            log.error("Error getting encode Number in stream", e);
            return new ArrayList<>();
        }
    }

    public String encodeNumber(Long number) {
        StringBuilder stringBuilder = new StringBuilder();
        while (number > 0) {
            stringBuilder.append(solWord.charAt((int) (number % solWordLength)));
            number /= solWordLength;
        }
        return stringBuilder.toString();
    }
}