package ru.gnivc.logistservice.kafka.consumer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConsumerTopicConfig {

    @Bean
    public NewTopic tripCarLocationTopic(){
        return TopicBuilder.name("trip_car_location")
                .build();
    }
    @Bean
    public NewTopic tripEventTopic(){
        return TopicBuilder.name("trip_event")
                .build();
    }
}
