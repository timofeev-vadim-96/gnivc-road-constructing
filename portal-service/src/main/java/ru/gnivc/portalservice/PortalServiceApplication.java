package ru.gnivc.portalservice;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.ClientResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import ru.gnivc.portalservice.dao.KeycloakCompaniesDao;
import ru.gnivc.portalservice.service.KeycloakService;

@SpringBootApplication
@EnableDiscoveryClient
public class PortalServiceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(PortalServiceApplication.class, args);
//		KeycloakCompaniesDao bean = context.getBean(KeycloakCompaniesDao.class);
//		bean.createClient("FOO");
	}
}
