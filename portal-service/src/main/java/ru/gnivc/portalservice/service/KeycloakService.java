package ru.gnivc.portalservice.service;

import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.*;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.gnivc.portalservice.config.KeycloakProperties;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.util.ClientRole;
import ru.gnivc.portalservice.util.RealmRole;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class KeycloakService {
    private final RealmResource realm;
    private final UsersResource usersManager;
    private final ClientsResource clientsManager;

    public KeycloakService(Keycloak keycloak, KeycloakProperties properties) {
        realm = keycloak.realm(properties.realm);
        usersManager = realm.users();
        clientsManager = realm.clients();
    }
    private record RegistrationResult(UserRepresentation newUser, Response response, String userId) {
    }

    private RegistrationResult registerUser(UserDto userDto){
        UserRepresentation newUser = convertToUserRepresentation(userDto);
        Response response = usersManager.create(newUser);
        log.info(String.format("Response: %s %s%n", response.getStatus(), response.getStatusInfo()));
        log.info(response.getLocation().toString());

        String userId = CreatedResponseUtil.getCreatedId(response);
        log.info(String.format("User created with userId: %s%n", userId));
        return new RegistrationResult(newUser, response, userId);
    }

    public void updateUser(UserDto userDto, String email){
        UserRepresentation user = usersManager.searchByEmail(email, true).getFirst();
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        UserResource userResource = usersManager.get(user.getId());
        userResource.update(user);
    }

    public String registerRealmUser(UserDto userDto, String password) {
        RegistrationResult registrationResult = registerUser(userDto);
        if (registrationResult.response.getStatus() == 201) {
            setPassword(registrationResult.userId, password);
            assignRealmRoleToUser(registrationResult.userId, RealmRole.ROLE_REGISTRATOR);
            return registrationResult.newUser.getUsername();
        }
        return null;
    }
    public String registerClientUser(UserDto userDto, String password, String clientId) {
        RegistrationResult registrationResult = registerUser(userDto);
        if (registrationResult.response.getStatus() == 201) {
            setPassword(registrationResult.userId, password);
            assignClientLevelRoleToUser(registrationResult.userId, clientId, userDto.getClientRole());
            return registrationResult.newUser.getUsername();
        }
        return null;
    }

    public UserRepresentation convertToUserRepresentation(UserDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(UUID.randomUUID().toString());
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        return user;
    }

    public void setPassword(String userId, String password) {
        CredentialRepresentation credentials = new CredentialRepresentation();
        credentials.setType(CredentialRepresentation.PASSWORD);
        credentials.setTemporary(false);
        credentials.setValue(password);

        UserResource userResource = usersManager.get(userId);
        userResource.resetPassword(credentials);
    }

    public UserRepresentation findUserById(String userId) {
        return usersManager.get(userId).toRepresentation();
    }

    public Optional<UserRepresentation> findUserByMail(String userMail) {
        return Optional.ofNullable(usersManager.searchByEmail(userMail, true).getFirst());
    }

    public void removeUser(String userId) {
        usersManager.delete(userId);
    }

    public void assignRealmRoleToUser(String userId, RealmRole role) {
        RoleRepresentation realmRole = realm.roles().get(role.name()).toRepresentation();
        usersManager.get(userId).roles().realmLevel().add(Collections.singletonList(realmRole));
    }

    public ClientRepresentation findClientById(String clientName) {
        return clientsManager.findByClientId(clientName).getFirst();
    }

    public void assignClientLevelRoleToUser(String userId, String clientId, ClientRole role) {
        RoleRepresentation clientLevelRole = clientsManager.get(clientId).roles().get(role.name()).toRepresentation();
        usersManager.get(userId).roles().clientLevel(clientId).add(Collections.singletonList(clientLevelRole));
    }

    public int createClient(String clientId) {
        ClientRepresentation clientRepresentation = new ClientRepresentation();
        clientRepresentation.setClientId(clientId);
        clientRepresentation.setPublicClient(false);
        clientRepresentation.setEnabled(true);
        clientRepresentation.setServiceAccountsEnabled(true);

        Response response = clientsManager.create(clientRepresentation);
        int statusCode = response.getStatus();
        log.info("HTTP Status of client creation = {}", statusCode);

        if (statusCode == 201) fillClientWithRoles(clientId);
        return statusCode;
    }

    public void fillClientWithRoles(String clientId) {
        ClientResource clientResource = clientsManager.get(clientId);
        log.info("client: {}", clientResource);
        for (ClientRole role : ClientRole.values()) {
            RoleRepresentation roleRepresentation = new RoleRepresentation();
            roleRepresentation.setName(role.name());
            roleRepresentation.setClientRole(true);
            roleRepresentation.setComposite(false);

            clientResource.roles().create(roleRepresentation);
        }
    }
}
