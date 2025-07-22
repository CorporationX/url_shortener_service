package faang.school.urlshortenerservice.config.context;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<UserHeaderFilter> userFilter(UserHeaderFilter f) {
        FilterRegistrationBean<UserHeaderFilter> reg = new FilterRegistrationBean<>(f);
        reg.addUrlPatterns("/api/*");

        return reg;
    }
}
