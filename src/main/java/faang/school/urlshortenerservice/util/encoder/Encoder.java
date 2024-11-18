package faang.school.urlshortenerservice.util.encoder;

import java.util.List;

public abstract class Encoder<T, H> {

    public List<H> encode(List<T> numbers) {
        return numbers.stream()
                .map(this::encode)
                .toList();
    }

    public abstract H encode(T t);
}
