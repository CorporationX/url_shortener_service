package faang.school.urlshortenerservice.config.shedlock;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Configuration
@RequiredArgsConstructor
public class ShedLockConfig {
    private final ShedLockProperties shedLockProperties;

    private LockProvider lockProvider(DataSource dataSource) {
        var configBuilder = JdbcTemplateLockProvider.Configuration.builder()
                .withJdbcTemplate(new JdbcTemplate(dataSource))
                .withTableName(shedLockProperties.getProvider().getJdbc().getTableName());
        if (shedLockProperties.getProvider().getJdbc().isUseDbTime()) {
            configBuilder.usingDbTime();
        }

        return new JdbcTemplateLockProvider(configBuilder.build());
    }
}
