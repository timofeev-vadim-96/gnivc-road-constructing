package ru.gnivc.logistservice.provider;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gnivc.logistservice.dto.input.CompanyDto;
import ru.gnivc.logistservice.dto.input.DriverDto;
import ru.gnivc.logistservice.dto.input.VehicleDto;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class PortalProvider {
    private final RestTemplate restTemplate;
    private final EurekaClient eurekaClient;

    public PortalProvider(EurekaClient eurekaClient) {
        this.restTemplate = new RestTemplate();
        this.eurekaClient = eurekaClient;
    }

    public VehicleDto getVehicleById(long vehicleId, String companyName) {
        String url = String.format(getPortalServiceIp() + "portal/v1/company/vehicle/%s?companyName=%s", vehicleId, companyName);

        ResponseEntity<VehicleDto> responseEntity = restTemplate.getForEntity(url, VehicleDto.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("Response status from portal-ms while trying to get vehicle is: {}", responseEntity.getStatusCode());
            return null;
        }
    }

    public DriverDto getDriverById(long driverId, String companyName) {
        String url = String.format(getPortalServiceIp() + "portal/v1/user/%s?companyName=%s", driverId, companyName);

        ResponseEntity<DriverDto> responseEntity = restTemplate.getForEntity(url, DriverDto.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("Response status from portal-ms while trying to get driver is: {}", responseEntity.getStatusCode());
            return null;
        }
    }

    public CompanyDto getCompanyByName(String companyName) {
        String url = String.format(getPortalServiceIp() + "portal/v1/company?companyName=%s", companyName);

        ResponseEntity<CompanyDto> responseEntity = restTemplate.getForEntity(url, CompanyDto.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            log.error("Response status from portal-ms while trying to get company is: {}", responseEntity.getStatusCode());
            return null;
        }
    }

    private String getPortalServiceIp(){
        Application application = eurekaClient.getApplication("PORTAL-MS");
        List<InstanceInfo> instanceInfos = application.getInstances();

        Random random = new Random();
        InstanceInfo randomInstance = instanceInfos.get(random.nextInt(instanceInfos.size()));
        return "http://" + randomInstance.getIPAddr() + ":" + randomInstance.getPort() + "/";
    }
}
