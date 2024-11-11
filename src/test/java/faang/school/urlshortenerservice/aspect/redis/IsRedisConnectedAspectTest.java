package faang.school.urlshortenerservice.aspect.redis;

import faang.school.urlshortenerservice.annotation.redis.IsRedisConnected;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IsRedisConnectedAspectTest {
    private static final Object OBJECT = new Object();

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private IsRedisConnected isRedisConnected;

    @InjectMocks
    private IsRedisConnectedAspect isRedisConnectedAspect;

    @Test
    void testIsRedisConnected_successful() throws Throwable {
        when(joinPoint.proceed()).thenReturn(OBJECT);
        assertThat(isRedisConnectedAspect.isRedisConnected(joinPoint, isRedisConnected))
                .isEqualTo(OBJECT);
        verify(joinPoint).proceed();
    }

    @Test
    void testIsRedisConnected_exception() throws Throwable {
        when(joinPoint.proceed()).thenThrow(new RuntimeException());
        assertThat(isRedisConnectedAspect.isRedisConnected(joinPoint, isRedisConnected))
                .isEqualTo(null);
        verify(joinPoint).proceed();
    }
}