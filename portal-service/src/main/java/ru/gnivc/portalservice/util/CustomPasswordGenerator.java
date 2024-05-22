package ru.gnivc.portalservice.util;

import lombok.RequiredArgsConstructor;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.gnivc.portalservice.config.UserPasswordProperties;

@Component
@RequiredArgsConstructor
public class CustomPasswordGenerator {
    private final UserPasswordProperties userPasswordProperties;

    /**
     * Метод генерации случайной последовательности символов
     */
    public String generatePassword(){
        return generatePassword(
                userPasswordProperties.length,
                userPasswordProperties.lowerCaseCharsQuantity,
                userPasswordProperties.upperCaseCharsQuantity,
                userPasswordProperties.digitsQuantity);
    }

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
}
