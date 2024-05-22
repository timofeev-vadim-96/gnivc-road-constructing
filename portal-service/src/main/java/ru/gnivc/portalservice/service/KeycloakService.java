package ru.gnivc.portalservice.service;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.keycloak.adapters.springsecurity.KeycloakAuthenticationException;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.gnivc.portalservice.config.KeycloakProperties;
import ru.gnivc.portalservice.dto.UserDto;
import ru.gnivc.portalservice.util.Role;

import java.util.List;

@Service
@Slf4j
public class KeycloakService {
    private final RealmResource realm;
    private final UsersResource usersResource;

    public KeycloakService(Keycloak keycloak, KeycloakProperties properties) {
        realm = keycloak.realm(properties.realm);
        usersResource = realm.users();
    }

    public int registerKeycloakUser(UserRepresentation newUser){
        Response response = usersResource.create(newUser);
        log.info(String.format("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo()));
        log.info(response.getLocation().toString());

        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info(String.format("User created with userId: %s%n", userId));
        return response.getStatus();
    }

    public UserRepresentation convertToUserRepresentation(UserDto userDto, List<Role> roles, String password){
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setValue(password);

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEmail(userDto.getEmail());
        userRepresentation.setFirstName(userDto.getFirstName());
        userRepresentation.setLastName(userDto.getLastName());
        userRepresentation.setRealmRoles(roles.stream().map(Enum::toString).toList());
        userRepresentation.setCredentials(List.of(new CredentialRepresentation()));
        userRepresentation.setEnabled(true);
        return userRepresentation;
    }
    public UserRepresentation convertToUserRepresentation(UserDto userDto, String password){
        return convertToUserRepresentation(userDto, List.of(Role.REGISTRATOR), password);
    }

    public UserRepresentation findUserById(String userId){
        return usersResource.get(userId).toRepresentation();
    }

    public void removeUser(String userId){
        usersResource.delete(userId);
    }
}
