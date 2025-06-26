package faang.school.urlshortenerservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final int MAX_PARAM_LENGTH = 100;
    private static final String[] SENSITIVE_PARAM_NAMES = {"password", "token", "secret", "key"};

    @Around("@within(org.springframework.web.bind.annotation.RestController) || " +
            "@within(org.springframework.stereotype.Service)")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = getOrCreateRequestId();
        MDC.put("requestId", requestId);
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        
        MDC.put("class", className);
        MDC.put("method", methodName);
        
        if (isController(joinPoint)) {
            String endpoint = getCurrentEndpoint();
            MDC.put("endpoint", endpoint);
            log.info("Incoming request to {} - {}.{}", endpoint, className, methodName);
        } else {
            log.debug("Entering {}.{}", className, methodName);
            if (log.isDebugEnabled()) {
                log.debug("Method parameters: {}", formatParameters(signature, joinPoint.getArgs()));
            }
        }
        
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (isController(joinPoint)) {
                log.info("Request completed in {}ms", executionTime);
            } else {
                log.debug("Method {}.{} completed in {}ms", className, methodName, executionTime);
            }
            return result;
        } catch (Exception e) {
            log.error("Error in {}.{}: {} - {}", 
                    className, methodName, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } finally {
            MDC.clear();
        }
    }

    private String getOrCreateRequestId() {
        String requestId = MDC.get("requestId");
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
        }
        return requestId;
    }

    private boolean isController(ProceedingJoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class);
    }

    private String getCurrentEndpoint() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            return attributes.getRequest().getRequestURI();
        }
        return "unknown";
    }

    private String formatParameters(MethodSignature signature, Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(signature.getParameterNames())
                .map(name -> {
                    int index = Arrays.asList(signature.getParameterNames()).indexOf(name);
                    Object value = args[index];
                    return formatParameter(name, value);
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String formatParameter(String name, Object value) {
        if (value == null) {
            return name + "=null";
        }

        if (isSensitiveParameter(name)) {
            return name + "=***";
        }

        String stringValue = value.toString();
        if (stringValue.length() > MAX_PARAM_LENGTH) {
            stringValue = stringValue.substring(0, MAX_PARAM_LENGTH) + "...";
        }
        return name + "=" + stringValue;
    }

    private boolean isSensitiveParameter(String name) {
        return Arrays.stream(SENSITIVE_PARAM_NAMES)
                .anyMatch(sensitive -> name.toLowerCase().contains(sensitive));
    }
} 