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
        String oldTranslatedStatus = translateStatus(oldStatus);
        String newTranslatedStatus = translateStatus(newStatus);

        String subject = "Indikatoriaus pokytis (%s -> %s)"
                .formatted(oldTranslatedStatus, newTranslatedStatus);

        String body = """
                Sveiki,
                
                Indikatorius: %s.
                %s

                Indikatoriaus būsena pasikeitė: %s į %s.
                Indikatoriaus reikšmė pasikeitė: %s į %s.
                """.formatted(indicatorName, indicatorDescription,
                    oldTranslatedStatus, newTranslatedStatus,
                    oldValue, newValue);

        userRepository.findAllByRole(ANALYST_ROLE)
                .stream()
                .map(User::getEmail)
                .distinct()
                .forEach(email -> {
                    try {
                        emailService.sendEmail(email, subject, body);
                    } catch (Exception exception) {
                        log.error("Failed to send indicator notification to={}", email, exception);
                    }
                });
    }

    private String translateStatus(IndicatorStatus status) {
        return switch (status) {
            case GREEN -> "ŽALIA";
            case YELLOW -> "GELTONA";
            case RED -> "RAUDONA";
            case UNKNOWN -> "NEŽINOMA";
        };
    }
}