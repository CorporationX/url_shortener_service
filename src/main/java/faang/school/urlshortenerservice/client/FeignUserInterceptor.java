package faang.school.urlshortenerservice.client;

import faang.school.urlshortenerservice.config.context.UserContext;
import faang.school.urlshortenerservice.exeption.InvalidUserContextException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        try {
            Long userId = userContext.getUserId();
            template.header("x-user-id", String.valueOf(userId));
        } catch (IllegalArgumentException ex) {
            log.error("Failed to set 'x-user-id' header: {}", ex.getMessage());
            throw new InvalidUserContextException("UserContext is invalid. Ensure headers are set correctly.", ex);
        }
    }
}