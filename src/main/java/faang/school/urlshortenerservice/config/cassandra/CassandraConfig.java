//package faang.school.urlshortenerservice.config.cassandra;
//
//import com.datastax.oss.driver.api.core.CqlSession;
//import com.datastax.oss.driver.api.core.CqlSessionBuilder;
//import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
//import org.springframework.data.cassandra.config.SchemaAction;
//import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
//
//import java.net.InetSocketAddress;
//import java.util.Optional;
//
//@Configuration
//@RequiredArgsConstructor
//@EnableCassandraRepositories(basePackages = "faang.school.urlshortenerservice.repository.cassandra")
//@Slf4j
//public class CassandraConfig extends AbstractCassandraConfiguration {
//
//    @Value("${spring.cassandra.contact-points}")
//    private String contactPoints;
//
//    @Value("${spring.cassandra.port}")
//    private int port;
//
//    @Value("${spring.cassandra.keyspace-name}")
//    private String keyspaceName;
//
//    @Value("${spring.cassandra.local-datacenter}")
//    private String localDatacenter;
//
//    @Override
//    protected String getKeyspaceName() {
//        return keyspaceName;
//    }
//
//    @Override
//    protected String getContactPoints() {
//        return contactPoints;
//    }
//
//    @Override
//    protected int getPort() {
//        return port;
//    }
//
//    @Override
//    protected String getLocalDataCenter() {
//        return localDatacenter;
//    }
//
//    @Bean
//    public CqlSession cqlSession(CqlSessionBuilder cqlSessionBuilder) {
//        CqlSession session = CqlSession.builder()
//                .addContactPoint(new InetSocketAddress(getContactPoints(), getPort()))
//                .withLocalDatacenter(getLocalDataCenter())
//                .build();
//
//        Optional<KeyspaceMetadata> keyspaceMetadata = session.getMetadata().getKeyspace(keyspaceName);
//        if (keyspaceMetadata.isEmpty()) {
//            log.info("Keyspace '{}' does not exist. Creating it now.", keyspaceName);
//            String cql = "CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};";
//            session.execute(String.format(cql, keyspaceName));
//            log.info("Keyspace '{}' created successfully.", keyspaceName);
//        } else {
//            log.info("Keyspace '{}' already exists.", keyspaceName);
//        }
//
//        return session;
//    }
//
//    @Override
//    public SchemaAction getSchemaAction() {
//        return SchemaAction.CREATE_IF_NOT_EXISTS;
//    }
//}
