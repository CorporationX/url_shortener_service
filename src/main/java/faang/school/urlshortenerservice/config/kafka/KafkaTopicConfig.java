package faang.school.urlshortenerservice.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    @Value("${spring.kafka.topics.short-url-visit.name}")
    private String topicUrlMappingVisitName;

    @Bean
    public NewTopic urlMappingClick() {
        return TopicBuilder.name(topicUrlMappingVisitName).build();
    }
}
