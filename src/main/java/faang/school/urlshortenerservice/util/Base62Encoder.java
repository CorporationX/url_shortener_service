package faang.school.urlshortenerservice.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Base62Encoder {

	private static final String BASE_62_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public List<String> encode(List<Long> numbers) {
		return numbers.stream()
				.map(num -> applyBase62Encoding(num))
				.toList();
	}

	private String applyBase62Encoding(Long number) {
		StringBuilder result = new StringBuilder();
		while (number > 0) {
			result.append(BASE_62_CHARACTERS.charAt((int) (number % BASE_62_CHARACTERS.length())));
			number /= BASE_62_CHARACTERS.length();
		}
		return result.toString();
	}
}