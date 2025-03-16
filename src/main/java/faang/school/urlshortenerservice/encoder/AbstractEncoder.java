package faang.school.urlshortenerservice.encoder;

import java.util.List;

public abstract class AbstractEncoder implements Encoder {

    static private final int STRING_MIX_PARAM = 3;
    private final String baseChars;

    public AbstractEncoder(String baseChars) {
        this.baseChars = baseChars;
    }

    protected String commonEncode(Long number) {
        if (number == 0) {
            return String.valueOf(baseChars.charAt(0));
        }

        int charsBase = baseChars.length();

        StringBuilder encodedString = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % charsBase);
            encodedString.insert(0, baseChars.charAt(remainder));
            number /= charsBase;
        }
        return mixString(encodedString.reverse().toString(), STRING_MIX_PARAM);
    }

    protected List<String> commonEncode(List<Long> numbers) {
        return numbers.stream()
                .map(this::commonEncode)
                .toList();
    }

    protected String mixString(String input, int shift) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        int length = input.length();
        shift = shift % length;
        int splitIndex = shift;

        return input.substring(splitIndex) + input.substring(0, splitIndex);
    }
}
