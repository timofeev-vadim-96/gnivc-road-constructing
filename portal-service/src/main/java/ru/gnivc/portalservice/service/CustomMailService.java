package ru.gnivc.portalservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CustomMailService {
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String from;

    public boolean sendSimpleEmail(String toAddress, String subject, String message) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(toAddress);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(message);
            simpleMailMessage.setFrom(from);
            emailSender.send(simpleMailMessage);
            return true;
        } catch (MailException e) {
            return false;
        }
    }

    public void sendEmailWithAttachment(String toAddress, String subject, String message, String attachment) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(toAddress);
            messageHelper.setSubject(subject);
            messageHelper.setText(message);
            FileSystemResource file = new FileSystemResource(ResourceUtils.getFile(attachment));
            messageHelper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Exception while trying to send a message. Attachment was not found.", e);
        } catch (MessagingException e) {
            throw new RuntimeException("Exception while trying to send a message to the mail: " + toAddress, e);
        }
    }
}
