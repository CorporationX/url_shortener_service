package faang.school.urlshortenerservice.interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UrlCachingInterceptor implements MethodInterceptor {

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        String hash = (String) invocation.getArguments()[0];
        log.info("Received request to get original URL for hash={}", hash);
        String originalUrl = (String) invocation.proceed();
        log.info("Found original URL={} for hash={}", originalUrl, hash);
        return originalUrl;
    }
}
