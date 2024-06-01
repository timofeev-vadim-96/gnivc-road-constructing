package ru.gnivc.portalservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.portalservice.dto.input.ResetPasswordDto;
import ru.gnivc.portalservice.dto.input.UserDto;
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
    public ResponseEntity<String> registerRealmUser(@Valid @RequestBody UserDto userDto){
        return userService.createRegistrator(userDto);
    }

    /**
     * Changing the user
     */
    @PutMapping()
    public ResponseEntity<String> editUser(@Valid @RequestBody UserDto userDto, @RequestParam String email){
        return userService.editUser(email, userDto);
    }

    /**
     * Changing the password (here we send the recovery code to the mail)
     */
    @GetMapping("/password/reset-request")
    public ResponseEntity<String> sendPasswordResetCode(@RequestParam String email){
        return userService.sendPasswordResetCode(email);
    }

    /**
     * Setting a new password for non-authenticated users using the recovery code from the mail
     */
    @PostMapping("/password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto,
                                            @RequestParam String email){
        return userService.resetPassword(email, resetPasswordDto);
    }

    /**
     * Password change by an authenticated user
     */
    @PutMapping("/password")
    public ResponseEntity<String> setPassword(@RequestParam String newPassword, @RequestParam String email){
        return userService.setPassword(email, newPassword);
    }
}
