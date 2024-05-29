package ru.gnivc.portalservice.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordDto {
    @NotNull
    private String resetCode;
    @NotNull
    private String newPassword;
}
