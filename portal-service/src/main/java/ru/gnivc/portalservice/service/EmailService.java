package ru.gnivc.portalservice.service;

public interface EmailService {
    boolean sendSimpleEmail(String toAddress, String subject, String message);
}
