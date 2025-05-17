package faang.school.urlshortenerservice.client;

import faang.school.urlshortenerservice.dto.UserAuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/users/auth/{username}")
    Optional<UserAuthDto> getUser(@PathVariable String username);
}