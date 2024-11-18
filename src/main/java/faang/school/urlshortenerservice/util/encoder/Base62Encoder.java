package faang.school.urlshortenerservice.util.encoder;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder extends Encoder<Long, Hash> {

    private static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE_62_LENGTH = BASE_62_CHARACTERS.length();

    @Value("${server.hash.generate.length}")
    private int hashLength;

    @Override
    public Hash encode(Long number) {
        if (number <= 0) {
            return new Hash(number.toString());
        }

        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_LENGTH)));
            number /= BASE_62_LENGTH;
        }

        if (builder.length() < hashLength) {
            int count = hashLength - builder.length();
            String buffer = "0".repeat(count);
            builder.append(buffer);
        }

        String hash = builder.reverse().toString();
        return new Hash(hash);
    }
}
