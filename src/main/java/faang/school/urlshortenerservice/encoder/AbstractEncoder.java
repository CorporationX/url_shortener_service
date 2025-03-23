package faang.school.urlshortenerservice.encoder;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public abstract class AbstractEncoder implements Encoder {

    private final String baseChars;
    private final Integer mixParameter;

    public AbstractEncoder(String baseChars, Integer mixParameter) {
        this.baseChars = baseChars;
        this.mixParameter = mixParameter;
    }

    @Override
    public String encode(Long sequenceNumber) {
        return commonEncode(sequenceNumber);
    }

    @Override
    public List<String> encode(List<Long> sequenceNumbers) {
        return commonEncode(sequenceNumbers);
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
        return mixString(encodedString.reverse().toString(), mixParameter);
    }

    protected List<String> commonEncode(List<Long> numbers) {
        return numbers.stream()
                .map(this::commonEncode)
                .toList();
    }

    protected String mixString(String input, int shift) {

        if (StringUtils.isBlank(input)) {
            return "";
        }
        int length = input.length();
        shift = shift % length;
        int splitIndex = shift;

        return input.substring(splitIndex) + input.substring(0, splitIndex);
    }
}
