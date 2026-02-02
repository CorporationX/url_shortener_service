package faang.school.urlshortenerservice.exception.handler;

import faang.school.urlshortenerservice.exception.UrlNotFoundException;
import faang.school.urlshortenerservice.exception.UrlNotValidException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/test")
public class TestController {

    @PostMapping("/valid")
    public void validate(@Valid @RequestBody TestDto dto) {}

    @GetMapping("/valid/constraint")
    public void validateConstraints() {
        throw new ConstraintViolationException("Name cannot be null, empty or a space", Set.of());
    }

    @GetMapping("/url/valid")
    public void throwUrlNotValid() {
        throw new UrlNotValidException("Invalid URL!");
    }

    @GetMapping("/url/hash")
    public void throwUrlNotFound() {
        throw new UrlNotFoundException("The URL for this hash does not exist");
    }

    @GetMapping("/internal")
    public void throwUnexpected() {
        throw new RuntimeException();
    }
}
