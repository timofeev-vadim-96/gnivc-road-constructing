package ru.gnivc.logistservice.kafka.producer.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerTopicConfig {

    @Bean
    public NewTopic companyStatisticsTopic(){
        return TopicBuilder.name("company_statistics")
                .build();
    }
}
