package faang.school.urlshortenerservice.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Base62Encoder {

  public static final String BASE_62_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static final int BASE_LENGTH = BASE_62_CHARACTERS.length();

  public List<String> encode(List<Long> numbers) {
    return numbers.stream()
        .map(number -> {
          StringBuilder result = new StringBuilder();
          do {
            result.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_LENGTH)));
            number /= BASE_LENGTH;
          } while (number > 0);
          return result.reverse().toString();
        })
        .collect(Collectors.toList());
  }
}
