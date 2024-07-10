package faang.school.urlshortenerservice;

import faang.school.urlshortenerservice.generator.hash.HashGenerator;
import faang.school.urlshortenerservice.service.hash.HashFreeService;
import faang.school.urlshortenerservice.service.hash.HashService;
import faang.school.urlshortenerservice.service.uniquenumber.UniqueNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class test {
    private final HashService hashService;
    private final HashGenerator hashGenerator;
    private final HashFreeService hashFreeService;
    private final UniqueNumber uniqueNumber;

    @GetMapping("/{userId}")
    public void getUser(@PathVariable("userId") long userId) {
        hashFreeService.getHashBatch();
    }
}
