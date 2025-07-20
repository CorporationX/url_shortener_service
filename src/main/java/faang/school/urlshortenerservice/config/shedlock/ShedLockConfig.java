package faang.school.urlshortenerservice.config.shedlock;

import faang.school.urlshortenerservice.config.properties.ShedLockProperties;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "${shedlock.defaults.lock-at-most-for}")
@EnableConfigurationProperties(ShedLockProperties.class)
@RequiredArgsConstructor
public class ShedLockConfig {
    private final ShedLockProperties properties;

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        var configBuilder = JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName(properties.getProvider().getJdbc().getTableName());
        if (properties.getProvider().getJdbc().isUseDbTime()) {
            configBuilder = configBuilder.usingDbTime();
        }
        return new JdbcTemplateLockProvider(configBuilder.build());
    }
}