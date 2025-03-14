package faang.school.urlshortenerservice.service;

import java.util.List;

public interface BaseEncoderService {

    List<String> encode(List<Long> numbers);
}