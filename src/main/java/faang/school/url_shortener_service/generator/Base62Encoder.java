package faang.school.url_shortener_service.generator;

import faang.school.url_shortener_service.entity.Hash;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Base62Encoder {

    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public List<Hash> encode(List<Long> numbers) {
        List<Hash> hashes = new ArrayList<>();
        numbers.forEach(n -> hashes.add(applyBase62Encoder(n)));
        return hashes;
    }

    private Hash applyBase62Encoder(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
            number /= BASE_62_CHARACTERS.length();
        }
        return new Hash(builder.reverse().toString());
    }
}