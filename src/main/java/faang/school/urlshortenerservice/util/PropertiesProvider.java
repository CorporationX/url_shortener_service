package faang.school.urlshortenerservice.util;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class PropertiesProvider implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@Nonnull final ApplicationContext applicationContext) throws BeansException {
        PropertiesProvider.applicationContext = applicationContext;
    }

    public static <T> T getProperty(final String property, final Class<T> clazz) {
        return applicationContext.getEnvironment().getProperty(property, clazz);
    }
}
