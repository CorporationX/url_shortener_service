package faang.school.urlshortenerservice.service.generator.encoder;

import org.springframework.stereotype.Component;

@Component
public class Base62Encoder {
  public static final String BASE_62_CHARACTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

  public String applyBase62Encoding(long number) {
    StringBuilder result = new StringBuilder();

    if (number == 0) {
      return String.valueOf(BASE_62_CHARACTER.charAt(0));
    }

    while (number > 0) {
      result.append(BASE_62_CHARACTER.charAt((int) (number % BASE_62_CHARACTER.length())));
      number /= BASE_62_CHARACTER.length();
    }

    return result.toString();
  }
}
