package faang.school.urlshortenerservice.config.shedlock;

import faang.school.urlshortenerservice.config.properties.ShedLockProperties;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "${shedlock.defaults.lock-at-most-for}")
@RequiredArgsConstructor
public class ShedLockConfig {
    private final ShedLockProperties props;

    @Bean
    public LockProvider lockProvider(DataSource dataSource) {
        var configBuilder = JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName(props.getProvider().getJdbc().getTableName());

        if (props.getProvider().getJdbc().isUseDbTime()) {
            configBuilder = configBuilder.usingDbTime();
        }

        return new JdbcTemplateLockProvider(configBuilder.build());
    }
}