package com.leftovers.kri.notification;

import com.leftovers.kri.indicator.IndicatorStatus;
import com.leftovers.kri.user.User;
import com.leftovers.kri.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorNotificationService {
    private static final String ANALYST_ROLE = "ANALYST";

    private final EmailService emailService;
    private final UserRepository userRepository;

    public void sendNotification(String indicatorName, String indicatorDescription,
                                 IndicatorStatus oldStatus, IndicatorStatus newStatus,
                                 Double oldValue, Double newValue) {
        String oldStatusDisplayName = oldStatus.getDisplayName();
        String newStatusDisplayName = newStatus.getDisplayName();

        String subject = buildSubject(oldStatusDisplayName, newStatusDisplayName);
        String body = buildBody(indicatorName, indicatorDescription, oldStatusDisplayName, newStatusDisplayName, oldValue, newValue);

        userRepository.findAllByRole(ANALYST_ROLE)
                .stream()
                .map(User::getEmail)
                .distinct()
                .forEach(email -> {
                    try {
                        emailService.sendEmail(email, subject, body);
                        log.info("Email sent to={} with subject={}", email, subject);
                    } catch (Exception exception) {
                        log.error("Failed to send indicator notification to={}", email, exception);
                    }
                });
    }

    private String buildSubject(String oldStatus, String newStatus) {
        return "Indikatoriaus pokytis (%s -> %s)".formatted(oldStatus, newStatus);
    }

    private String buildBody(String indicatorName, String indicatorDescription,
                             String oldStatus, String newStatus,
                             Double oldValue, Double newValue) {
        return """
                Sveiki,
                
                Indikatorius: %s.
                %s

                Indikatoriaus būsena pasikeitė: %s į %s.
                Indikatoriaus reikšmė pasikeitė: %s į %s.
                """.formatted(indicatorName, indicatorDescription,
                oldStatus, newStatus,
                oldValue, newValue);
    }
}