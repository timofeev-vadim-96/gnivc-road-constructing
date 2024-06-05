package ru.gnivc.logistservice.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import ru.gnivc.logistservice.dto.input.TripLocationDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTripLocationConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Конфигурация консюмера
     */
    public Map<String, Object> consumerConfig(){
        HashMap<String, Object> props = new HashMap<>();
        //конфиг сервера (его ip и порт)
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //ключ тоже будет строкой
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        //будем отправлять строки брокеру
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TripEventDeserializer.class);
        return props;
    }

    /**
     * Фабрика слушателей
     */
    @Bean
    public ConsumerFactory<String, TripLocationDto> tripLocationPointsConsumerFactory(){
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    /**
     * Контейнер для слушателей точек геопозиции рейса
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, TripLocationDto>>
    tripLocationListenerContainerFactory(ConsumerFactory<String, TripLocationDto> consumerFactory){
        ConcurrentKafkaListenerContainerFactory<String, TripLocationDto>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
