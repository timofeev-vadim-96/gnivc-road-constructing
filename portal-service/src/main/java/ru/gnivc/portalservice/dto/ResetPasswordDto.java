package ru.gnivc.portalservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordDto {
    private String resetCode;
    private String newPassword;
}
