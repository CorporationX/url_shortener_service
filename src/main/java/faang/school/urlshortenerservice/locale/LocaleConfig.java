package faang.school.urlshortenerservice.locale;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class LocaleConfig {
    @Bean
    public FilterRegistrationBean<LocaleChangeFilter> localeChangeFilter() {
        FilterRegistrationBean<LocaleChangeFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LocaleChangeFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}