package ru.gnivc.logistservice.kafka.config;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import ru.gnivc.logistservice.dto.input.TripLocationDto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

@Component
public class TripLocationDeserializer implements Deserializer<TripLocationDto> {
    @Override
    public TripLocationDto deserialize(String s, byte[] bytes) {
        try (ByteArrayInputStream in = new ByteArrayInputStream(bytes)){
            ObjectInputStream objectIn = new ObjectInputStream(in);
            TripLocationDto event = (TripLocationDto) objectIn.readObject();
            objectIn.close();
            return event;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
