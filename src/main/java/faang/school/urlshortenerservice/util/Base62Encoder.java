package faang.school.urlshortenerservice.util;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {

  private final static String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public List<String> encode(List<Long> numbers) {
    return numbers.stream()
        .map(this::applyBase62Encoding)
        .toList();
  }

  private String applyBase62Encoding(long number) {
    StringBuilder stringBuilder = new StringBuilder(1);
    int base = BASE_62_CHARACTERS.length();
    do {
      stringBuilder.insert(0, BASE_62_CHARACTERS.charAt((int) (number % base)));
      number /= base;
    } while (number > 0);
    return stringBuilder.toString();
  }
}
