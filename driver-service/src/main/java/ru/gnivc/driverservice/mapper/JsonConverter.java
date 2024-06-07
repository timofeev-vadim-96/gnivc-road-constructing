package ru.gnivc.driverservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
@RequiredArgsConstructor
public class JsonConverter {
    private final JsonMapper jsonMapper;

    public JsonConverter() {
        jsonMapper = new com.fasterxml.jackson.databind.json.JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public <T> String serializeToJson(T data) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(data);
    }
}
