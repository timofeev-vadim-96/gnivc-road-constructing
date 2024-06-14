package ru.gnivc.driverservice.provider;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.driverservice.dto.input.TaskDto;
import ru.gnivc.driverservice.dto.input.TripDto;

import java.util.*;

@Service
@Slf4j
public class LogistProvider {
    private final RestTemplate restTemplate;
    private final EurekaClient eurekaClient;

    public LogistProvider(EurekaClient eurekaClient) {
        this.restTemplate = new RestTemplate();
        this.eurekaClient = eurekaClient;
    }

    public TripDto getTripById(long tripId, String companyName) {
        String url = String.format(getPortalServiceIp() + "/logist/v1/trip/%d?companyName=%s", tripId, companyName);

        ResponseEntity<TripDto> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                TripDto.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("Response status from logist-ms while trying to get trip by id: {}", responseEntity.getStatusCode());
            return null;
        }
    }

    public List<TaskDto> getTasksByDriverId(long driverId, String companyName) {
        String url = String.format(getPortalServiceIp() + "/logist/v1/task/byDriver/%d?companyName=%s", driverId, companyName);

        ResponseEntity<List<TaskDto>> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("Response status from logist-ms while trying to get driver's tasks is: {}", responseEntity.getStatusCode());
            return null;
        }
    }

    public TripDto createTrip(long taskId, String companyName) {
        String url = String.format(getPortalServiceIp() + "/logist/v1/trip?taskId=%d&companyName=%s", taskId, companyName);

        ResponseEntity<TripDto> responseEntity = restTemplate.exchange(url, HttpMethod.POST, null, TripDto.class);

        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            return responseEntity.getBody();
        } else {
            log.error("Response status from logist-ms while trying to get driver's tasks is: {}", responseEntity.getStatusCode());
            return null;
        }
    }

    private String getPortalServiceIp() {
        Application application = eurekaClient.getApplication("LOGIST-MS");
        List<InstanceInfo> instanceInfos = application.getInstances();

        Random random = new Random();
        InstanceInfo randomInstance = instanceInfos.get(random.nextInt(instanceInfos.size()));
        return "http://" + randomInstance.getIPAddr() + ":" + randomInstance.getPort() + "/";
    }
}
