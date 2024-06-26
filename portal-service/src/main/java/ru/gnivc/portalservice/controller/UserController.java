package ru.gnivc.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.gnivc.portalservice.dto.input.ResetPasswordDto;
import ru.gnivc.portalservice.dto.input.UserDto;
import ru.gnivc.portalservice.model.UserEntity;
import ru.gnivc.portalservice.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal/v1/user")
public class UserController {
    private final UserService userService;

    /**
     * Creating a realm user
     */
    @PostMapping()
    public ResponseEntity<String> registerRealmUser(@Valid @RequestBody UserDto userDto) {
        return userService.createRegistrator(userDto);
    }

    /**
     * Changing the user
     */
    @PutMapping()
    public ResponseEntity<String> editUser(@Valid @RequestBody UserDto userDto, @RequestParam String email) {
        return userService.editUser(email, userDto);
    }

    /**
     * Changing the password (here we send the recovery code to the mail)
     */
    @GetMapping("/password/reset-request")
    public ResponseEntity<String> sendPasswordResetCode(@RequestParam String email) {
        return userService.sendPasswordResetCode(email);
    }

    /**
     * Setting a new password for non-authenticated users using the recovery code from the mail
     */
    @PostMapping("/password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                                @RequestParam String email) {
        return userService.resetPassword(email, resetPasswordDto);
    }

    /**
     * Password change by an authenticated user
     */
    @PutMapping("/password")
    public ResponseEntity<String> setPassword(@RequestParam String newPassword, @RequestParam String email) {
        return userService.setPassword(email, newPassword);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable long userId,
                                                  @RequestParam String companyName) {
        return userService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeUserById(@PathVariable long userId,
                                               @RequestParam String companyName) {
        return userService.removeUserById(userId);
    }
}
