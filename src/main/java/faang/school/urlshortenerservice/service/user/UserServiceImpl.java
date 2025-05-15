package faang.school.urlshortenerservice.service.user;

import faang.school.urlshortenerservice.client.UserServiceClient;
import faang.school.urlshortenerservice.dto.UserAuthDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserServiceClient userServiceClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserData(username)
                .map(user -> new User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getRoles()
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Failed to retrieve user: " + username));
    }

    @Retryable(
            retryFor = { FeignException.FeignServerException.class },
            backoff = @Backoff(delay = 1000, multiplier = 3)
    )
    private Optional<UserAuthDto> getUserData(String username) {
        return userServiceClient.getUser(username);
    }

    @Recover
    private UserAuthDto recover(FeignException.FeignClientException e, long userId) {
        throw new RuntimeException("Couldn't get user with id " + userId + " after repeated attempts", e);
    }
}
