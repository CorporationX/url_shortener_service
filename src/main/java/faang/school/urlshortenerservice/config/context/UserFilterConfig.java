package faang.school.urlshortenerservice.config.context;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserFilterConfig {
    @Bean
    public FilterRegistrationBean<UserHeaderFilter> userFilter(UserHeaderFilter filter) {
        FilterRegistrationBean<UserHeaderFilter> reg = new FilterRegistrationBean<>(filter);
        reg.addUrlPatterns("/api/*");

        return reg;
    }
}
