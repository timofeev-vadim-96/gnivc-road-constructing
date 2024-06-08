package ru.gnivc.logistservice.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gnivc.logistservice.dto.input.TripEventDto;
import ru.gnivc.logistservice.dto.input.TripLocationDto;

import java.text.SimpleDateFormat;


@Component
@RequiredArgsConstructor
public class JsonConverter {
    private final com.fasterxml.jackson.databind.json.JsonMapper jsonMapper;

    public JsonConverter() {
        jsonMapper = new JsonMapper();
        jsonMapper.registerModule(new JavaTimeModule());
        jsonMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public TripEventDto getTripEventDto(String json) throws JsonProcessingException {
        return jsonMapper.readValue(json, TripEventDto.class);
    }
    public TripLocationDto getTripLocationDto(String json) throws JsonProcessingException {
        return jsonMapper.readValue(json, TripLocationDto.class);
    }

    public <T> String serializeToJson(T data) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(data);
    }
}
