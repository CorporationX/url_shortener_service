package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.RequestUlrDto;
import faang.school.urlshortenerservice.dto.ResponseUrlDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlService {

    public ResponseUrlDto shorten(@Valid RequestUlrDto requestUlrDto) {

        return null;
    }
}
