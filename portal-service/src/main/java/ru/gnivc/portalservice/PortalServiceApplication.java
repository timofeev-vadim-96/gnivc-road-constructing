package ru.gnivc.portalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.gnivc.portalservice.config.KeycloakProperties;
import ru.gnivc.portalservice.dto.UserDto;
import ru.gnivc.portalservice.service.KeycloakService;
import ru.gnivc.portalservice.service.UserService;

@SpringBootApplication
@EnableDiscoveryClient
public class PortalServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(PortalServiceApplication.class, args);

//		UserService service = context.getBean(UserService.class);
//		service.createNewUser(new UserDto("some@email.com", "testUser", "password"));
	}

}
