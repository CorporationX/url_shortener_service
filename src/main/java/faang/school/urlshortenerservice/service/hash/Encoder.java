package faang.school.urlshortenerservice.service.hash;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class Encoder {

    public List<String> encodeNumbers(List<Integer> numbers) {
        log.info("Trying to encode {} numbers", numbers.size());
        return numbers.stream()
                .map(this::encodeNumber)
                .toList();
    }

    protected abstract String encodeNumber(int number);
}
