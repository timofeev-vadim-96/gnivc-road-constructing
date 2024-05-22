package ru.gnivc.portalservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gnivc.portalservice.dto.ResetPasswordDto;
import ru.gnivc.portalservice.dto.UserDto;

@RestController
@RequestMapping("/portal/v1")
public class MainController {

    /**
     * Создание пользователя
     */
    @PostMapping("/user")
    public ResponseEntity<Void> createUser(@RequestBody UserDto userDto){
        return null;
    }

    /**
     * Смена пароля (здесь отправляем на почту код восстановления)
     */
    @GetMapping("/password/reset-request/{login}")
    public ResponseEntity<Void> resetPassword(@PathVariable String login){
        return null;
    }

    /**
     * Установка нового пароля
     */
    @PostMapping("/password")
    public ResponseEntity<Void> setPassword(@RequestBody ResetPasswordDto resetPasswordDto){
        return null;
    }

    /**
     * Смена пароля
     */
    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(){
        return null;
    }

    /**
     * Создание компании
     */
    @PostMapping("/company")
    public ResponseEntity<Void> createCompany(){
        return null;
    }

    /**
     * Обновление компании
     */
    @PutMapping("/company")
    public ResponseEntity<Void> updateCompany(){
        return null;
    }

    /**
     * Удаление компании
     */
    @DeleteMapping("/company")
    public ResponseEntity<Void> removeCompany(){
        return null;
    }

    /**
     * Просмотр компании
     */
    @GetMapping("/company/{id}")
    public ResponseEntity<Void> getCompany(@PathVariable long id){
        return null;
    }
}
