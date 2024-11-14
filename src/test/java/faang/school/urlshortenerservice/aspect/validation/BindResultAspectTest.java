package faang.school.urlshortenerservice.aspect.validation;

import faang.school.urlshortenerservice.annotation.validation.ValidateBindingResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BindResultAspectTest {
    private static final Object OBJECT = new Object();

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private ValidateBindingResult validateBindingResult;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private BindResultAspect bindResultAspect;

    @Test
    void testCheckingBindingResult_noHasErrors() throws Throwable {
        Object[] args = new Object[]{OBJECT, bindingResult};

        when(joinPoint.getArgs()).thenReturn(args);
        when(bindingResult.hasErrors()).thenReturn(false);
        when(joinPoint.proceed()).thenReturn(OBJECT);

        assertThat(bindResultAspect.checkBindingResult(joinPoint, validateBindingResult))
                .isEqualTo(OBJECT);
        verify(joinPoint).getArgs();
        verify(joinPoint).proceed();
    }

    @Test
    void testCheckingBindingResult_hasErrors() {
        Object[] args = new Object[]{OBJECT, bindingResult};

        when(joinPoint.getArgs()).thenReturn(args);
        when(bindingResult.hasErrors()).thenReturn(true);

        assertThatThrownBy(() -> bindResultAspect.checkBindingResult(joinPoint, validateBindingResult))
                .isInstanceOf(BindException.class)
                .isIn(bindingResult);
        verify(joinPoint).getArgs();
    }
}