package faang.school.urlshortenerservice.aspect.logging;

import faang.school.urlshortenerservice.annotation.logging.LogExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {
    private static final Object OBJECT = new Object();
    private static final String METHOD_NAME = "method";

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private LogExecution logExecution;

    @Mock
    private Signature signature;

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Test
    void testLogExecution_successful() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn(METHOD_NAME);
        when(joinPoint.proceed()).thenReturn(OBJECT);

        assertThat(loggingAspect.logExecution(joinPoint, logExecution))
                .isEqualTo(OBJECT);
        verify(joinPoint).proceed();
    }

    @Test
    void testLogExecution_exception() throws Throwable {
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn(METHOD_NAME);
        when(joinPoint.proceed()).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> loggingAspect.logExecution(joinPoint, logExecution))
                .isInstanceOf(RuntimeException.class);
        verify(joinPoint).proceed();
    }
}