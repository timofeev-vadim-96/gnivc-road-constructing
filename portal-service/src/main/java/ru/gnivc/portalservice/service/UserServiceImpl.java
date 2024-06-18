package ru.gnivc.portalservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.gnivc.portalservice.dao.CustomQueriesDao;
import ru.gnivc.portalservice.dao.UserDao;
import ru.gnivc.portalservice.dto.input.ResetPasswordDto;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.dto.output.CompanyUserDto;
import ru.gnivc.portalservice.model.UserEntity;
import ru.gnivc.portalservice.util.CustomPasswordGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class UserServiceImpl implements UserService, CompanyUserService {
    private final KeycloakService keycloakService;

    private final CustomPasswordGenerator passwordGenerator;

    private final EmailService mailService;

    private final UserDao userDao;

    private final CustomQueriesDao customQueriesDao;

    private final CompanyUserBinder companyUserService;

    private final Map<String, PasswordResetBox> resetCodesByEmailMap = new ConcurrentHashMap<>();

    public ResponseEntity<UserEntity> getUserById(long id) {
        Optional<UserEntity> user = userDao.findById(id);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("User with id = %s not found", id));
        } else {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<String> editUser(String email, UserDto userDto) {
        Optional<UserEntity> userEntityWithNewEmail = userDao.findByEmail(userDto.getEmail());
        Optional<UserRepresentation> userInKeycloakWithNewEmail = keycloakService.findUserByMail(userDto.getEmail());

        Optional<UserEntity> userEntityWithOldEmail = userDao.findByEmail(email);
        Optional<UserRepresentation> userInKeycloakWithOldEmail = keycloakService.findUserByMail(email);
        String answer;
        if ((userEntityWithNewEmail.isPresent() || userInKeycloakWithNewEmail.isPresent()) &&
                !email.equals(userDto.getEmail())) {
            answer = String.format("The user with an email: %s is already exists. You can't change your" +
                    "email to someone else's", userDto.getEmail());
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        } else if (userEntityWithOldEmail.isEmpty() || userInKeycloakWithOldEmail.isEmpty()) {
            answer = String.format("The user with an email: %s was not found.", email);
            return new ResponseEntity<>(answer, HttpStatus.NOT_FOUND);
        } else {
            return updateUser(email, userDto, userEntityWithOldEmail.get());
        }
    }

    @Transactional
    public ResponseEntity<Void> removeUserById(long userId) {
        Optional<UserEntity> user = userDao.findById(userId);
        if (user.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("User with id = %s not found", userId));
        } else {
            userDao.delete(user.get());
            keycloakService.removeUser(user.get().getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<String> createRegistrator(UserDto userDto) {
        Optional<UserEntity> user = userDao.findByEmail(userDto.getEmail());
        Optional<UserRepresentation> userInKeycloak = keycloakService.findUserByMail(userDto.getEmail());

        //if the user exists in the realm
        if (user.isPresent() || userInKeycloak.isPresent()) {
            String answer = "The user with mail " + userDto.getEmail() + " already exists.";
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        } else {
            save(userDto, true);
            String password = passwordGenerator.generatePassword();
            String username = keycloakService.registerRealmUser(userDto, password);
            return getRegistryResultAnswer(userDto, username, password);
        }
    }

    @Transactional
    public ResponseEntity<String> createClientUser(String companyName, UserDto userDto) {
        Optional<UserEntity> user = userDao.findByEmail(userDto.getEmail());
        Optional<UserRepresentation> userInKeycloak = keycloakService.findUserByMail(userDto.getEmail());

        if (user.isPresent() && userInKeycloak.isPresent()) {
            Optional<List<CompanyUserDto>> users = customQueriesDao.getCompanyUsers(companyName);
            if (users.isEmpty()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Company with name = " + companyName + " not found.");
            }
            Optional<CompanyUserDto> companyUser = users.get().stream()
                    .filter(usr -> usr.getEmail().equals(userDto.getEmail()))
                    .findFirst();
            String answer;
            if (companyUser.isPresent()) {
                answer = "The user with mail " + userDto.getEmail() +
                        " already exists and registered with this company";
                return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
            } else {
                return bindUserWithCompany(companyName, userDto, userInKeycloak.get());
            }
        } else {
            return saveClientUser(companyName, userDto);
        }
    }

    public List<CompanyUserDto> getCompanyUsers(String companyName) {
        Optional<List<CompanyUserDto>> users = customQueriesDao.getCompanyUsers(companyName);
        return users.orElse(null);
    }

    public ResponseEntity<String> setPassword(String email, String password) {
        Optional<UserRepresentation> user = keycloakService.findUserByMail(email);
        String answer;
        if (user.isEmpty()) {
            answer = String.format("The user with an email: %s was not found.", email);
            return new ResponseEntity<>(answer, HttpStatus.NOT_FOUND);
        } else {
            keycloakService.setPassword(user.get().getId(), password);
            return new ResponseEntity<>("The password has been changed", HttpStatus.OK);
        }
    }

    public ResponseEntity<String> resetPassword(String email, ResetPasswordDto resetPasswordDto) {
        LocalDateTime now = LocalDateTime.now();
        String answer;
        if (!resetPasswordDto.getResetCode().equals(resetCodesByEmailMap.get(email).resetCode)) {
            answer = "Invalid password recovery code";
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        } else if (resetCodesByEmailMap.get(email).issueTime.isBefore(now)) {
            answer = "The password reset code has expired";
            return new ResponseEntity<>(answer, HttpStatus.REQUEST_TIMEOUT);
        } else {
            return setPassword(email, resetPasswordDto.getNewPassword());
        }
    }

    public ResponseEntity<String> sendPasswordResetCode(String email) {
        String answer;
        Optional<UserRepresentation> user = keycloakService.findUserByMail(email);
        if (user.isEmpty()) {
            answer = String.format("The user with an email: %s was not found.", email);
            return new ResponseEntity<>(answer, HttpStatus.NOT_FOUND);
        } else {
            String resetCode = passwordGenerator.generateResetCode();
            LocalDateTime expirationDate = passwordGenerator.getExpirationDateFromNow();
            resetCodesByEmailMap.put(email, new PasswordResetBox(resetCode, expirationDate));
            mailService.sendSimpleEmail(
                    email,
                    "Request to change the password in GNIVC",
                    "We have received a request to change the password. If it wasn't you, " +
                            "then just ignore this email. \n" +
                            "To regenerate the password, use the code: " + resetCode);
            answer = "The password recovery code has been sent to your email: " + email;
            return new ResponseEntity<>(answer, HttpStatus.OK);
        }
    }

    /**
     * The method of clearing the map of expired password recovery codes
     */
    @Scheduled(fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    protected void cleanResetCodesBox() {
        LocalDateTime now = LocalDateTime.now();
        resetCodesByEmailMap.entrySet()
                .removeIf(entry -> entry.getValue().issueTime.isBefore(now));
    }

    private UserEntity updateUserEntity(UserDto userDto, UserEntity user) {
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        return user;
    }

    private boolean compareUsersAfterUpdate(
            UserEntity user,
            Optional<UserRepresentation> userRepresentation) {

        if (userRepresentation.isEmpty()) {
            return false;
        } else {
            UserRepresentation updatedUser = userRepresentation.get();
            return user.getEmail().equals(updatedUser.getEmail()) &&
                    user.getFirstName().equals(updatedUser.getFirstName()) &&
                    user.getLastName().equals(updatedUser.getLastName());
        }
    }

    private record PasswordResetBox(String resetCode, LocalDateTime issueTime) {
    }

    private ResponseEntity<String> getRegistryResultAnswer(
            UserDto userDto,
            String username,
            String password) {

        boolean isSuccessful = mailService.sendSimpleEmail(
                userDto.getEmail(),
                "Your GNIVC credentials",
                String.format("Hi there! \n" +
                        "Your login is: %s\n" +
                        "Your password is: %s\n" +
                        "You can also use your email to log in.", username, password));

        String answer;
        if (isSuccessful) {
            answer = "The user is registered. Login and password has been sent to your mail: " + userDto.getEmail();
            return new ResponseEntity<>(answer, HttpStatus.CREATED);
        } else {
            answer = "Exception while trying to send a message to the mail: " + userDto.getEmail() +
                    ". Perhaps, it's doesn't exist";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, answer);
        }
    }

    private void save(UserDto userDto, boolean isRegistrator) {
        UserEntity userEntity = UserEntity.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .isRegistrator(isRegistrator)
                .build();
        userDao.save(userEntity);
    }

    private ResponseEntity<String> updateUser(
            String email,
            UserDto userDto,
            UserEntity userEntityWithOldEmail) {

        Optional<UserRepresentation> userInKeycloakWithNewEmail;
        String answer;
        UserEntity user = updateUserEntity(userDto, userEntityWithOldEmail);

        keycloakService.updateUser(userDto, email);
        userInKeycloakWithNewEmail = keycloakService.findUserByMail(userDto.getEmail());
        boolean isUpdated = compareUsersAfterUpdate(user, userInKeycloakWithNewEmail);
        if (!isUpdated) {
            answer = "There was a problem when trying to update user data";
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, answer);
        } else {
            userDao.save(user);
            answer = "The user's data has been updated";
            return new ResponseEntity<>(answer, HttpStatus.ACCEPTED);
        }
    }

    private ResponseEntity<String> saveClientUser(String companyName, UserDto userDto) {
        save(userDto, false);

        companyUserService.bindUserWithCompany(userDto.getEmail(), companyName, userDto.getClientRole());

        String password = passwordGenerator.generatePassword();
        Optional<String> username = keycloakService.registerClientUser(userDto, password, companyName);
        if (username.isEmpty()) {
            String answer = "Error when trying to register a user";
            return new ResponseEntity<>(answer, HttpStatus.BAD_REQUEST);
        } else {
            return getRegistryResultAnswer(userDto, username.get(), password);
        }
    }

    private ResponseEntity<String> bindUserWithCompany(
            String companyName,
            UserDto userDto,
            UserRepresentation userInKeycloak) {

        String answer;
        companyUserService.bindUserWithCompany(userDto.getEmail(), companyName, userDto.getClientRole());
        String userId = userInKeycloak.getId();
        keycloakService.assignClientLevelRoleToUser(userId, companyName, userDto.getClientRole());
        answer = "The user has been added to the company with the role: " + userDto.getClientRole().name();
        return new ResponseEntity<>(answer, HttpStatus.ACCEPTED);
    }
}
