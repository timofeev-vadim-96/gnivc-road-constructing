package ru.gnivc.portalservice.util;

import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;
import ru.gnivc.portalservice.config.ResetCodeProperties;
import ru.gnivc.portalservice.config.UserPasswordProperties;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomPasswordGenerator {
    private final UserPasswordProperties userPasswordProperties;
    private final ResetCodeProperties resetCodeProperties;

    /**
     * Password generation
     */
    public String generatePassword(){
        return generatePassword(
                userPasswordProperties.length,
                userPasswordProperties.lowerCaseCharsQuantity,
                userPasswordProperties.upperCaseCharsQuantity,
                userPasswordProperties.digitsQuantity);
    }

    /**
     * Generating the recovery code
     */
    public String generateResetCode(){
        return generatePassword(
                resetCodeProperties.length,
                resetCodeProperties.lowerCaseCharsQuantity,
                resetCodeProperties.upperCaseCharsQuantity,
                resetCodeProperties.digitsQuantity);
    }

    /**
     * Password generation
     */
    public String generatePassword(int passwordLength, int lowerCaseCharsQuantity, int upperCaseCharsQuantity, int digitsQuantity) {
        PasswordGenerator gen = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(lowerCaseCharsQuantity);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(upperCaseCharsQuantity);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(digitsQuantity);

        return gen.generatePassword(passwordLength, lowerCaseRule,
                upperCaseRule, digitRule);
    }

    /**
     * Generation of the recovery code validity period
     */
    public LocalDateTime getExpirationDateFromNow(){
        return LocalDateTime.now().plusMinutes(resetCodeProperties.expirationDateInMinutes);
    }
}
