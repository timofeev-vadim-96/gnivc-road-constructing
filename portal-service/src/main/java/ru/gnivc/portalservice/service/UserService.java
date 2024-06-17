package ru.gnivc.portalservice.service;

import org.springframework.http.ResponseEntity;
import ru.gnivc.portalservice.dto.input.ResetPasswordDto;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.dto.output.CompanyUserDto;
import ru.gnivc.portalservice.model.UserEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<UserEntity> getUserById(long id);
    ResponseEntity<String> editUser(String email, UserDto userDto);
    public ResponseEntity<Void> removeUserById(long userId);
    ResponseEntity<String> createRegistrator(UserDto userDto);
    ResponseEntity<String> setPassword(String email, String password);
    ResponseEntity<String> resetPassword(String email, ResetPasswordDto resetPasswordDto);
    ResponseEntity<String> sendPasswordResetCode(String email);
}
