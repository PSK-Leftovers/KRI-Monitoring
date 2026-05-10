package com.leftovers.kri.notification;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}
