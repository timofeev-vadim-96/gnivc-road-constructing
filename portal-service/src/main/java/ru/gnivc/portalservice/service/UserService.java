package ru.gnivc.portalservice.service;

import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import ru.gnivc.portalservice.dao.UserDao;
import ru.gnivc.portalservice.dto.UserDto;
import ru.gnivc.portalservice.model.CustomUser;
import ru.gnivc.portalservice.util.CustomPasswordGenerator;
import ru.gnivc.portalservice.util.Role;


@Service
@RequiredArgsConstructor
public class UserService {
    private final KeycloakService keycloakService;
    private final CustomPasswordGenerator passwordGenerator;

    private final UserDao dao;

    public String createNewUser(UserDto userDto){
        //saving the a to local database
        save(userDto);
        String password = passwordGenerator.generatePassword();
        //mapping + saving a user to keycloak
        UserRepresentation userRepresentation = keycloakService.convertToUserRepresentation(userDto, password);
        int statusCode = keycloakService.registerKeycloakUser(userRepresentation);
        if (statusCode == 201) {
            //todo отправка почты
            return "The user is registered. The login password has been sent to the mail: " + userDto.getEmail();
        } else {
            return "Error when trying to register a user";
        }
    }

    private void save(UserDto userDto, Role role){
        CustomUser customUser = CustomUser.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .role(role.toString())
                .build();
        dao.save(customUser);
    }
    private void save(UserDto userDto){
        save(userDto, Role.REGISTRATOR);
    }
}
