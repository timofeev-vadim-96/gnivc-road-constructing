package ru.gnivc.logistservice.kafka.producer.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.logistservice.dto.output.StatisticByCompanyDto;
import ru.gnivc.logistservice.mapper.JsonConverter;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String COMPANY_STAT_TOPIC = "company_statistics";
    private final JsonConverter jsonConverter;

    public ResponseEntity<Void> sendCompanyStatistics(Map<String, StatisticByCompanyDto> stat){
        try {
            String message = jsonConverter.serializeToJson(stat);
            SendResult<String, String> result = kafkaTemplate.send(COMPANY_STAT_TOPIC, message).get();
            if (result.getRecordMetadata() == null) {
                String answer = "Company statistics were not sent successfully";
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, answer);
            } else {
                return new ResponseEntity<>(HttpStatus.CREATED);
            }
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
