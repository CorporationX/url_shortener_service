package faang.school.urlshortenerservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/url")
@RestController
@RequiredArgsConstructor
public class UrlController {

    @Autowired
    private List<HttpMessageConverter<?>> converters;

    @GetMapping("/converters")
    public List<String> getConverters() {
        return converters.stream()
                .map(converter -> converter.getClass().getName())
                .toList();
    }
}
