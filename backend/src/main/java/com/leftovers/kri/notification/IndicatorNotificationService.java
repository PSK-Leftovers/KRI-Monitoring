package com.leftovers.kri.notification;

import com.leftovers.kri.indicator.IndicatorStatus;
import com.leftovers.kri.logging.Audited;
import com.leftovers.kri.user.User;
import com.leftovers.kri.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorNotificationService {
    private static final String DIRECTOR_ROLE = "DIRECTOR";

    private final EmailService emailService;
    private final UserRepository userRepository;

    @Audited(action = "SEND_STATUS_NOTIFICATION")
    public void sendNotification(String indicatorName, String indicatorDescription,
                                 IndicatorStatus oldStatus, IndicatorStatus newStatus,
                                 Double oldValue, Double newValue) {

        String oldStatusDisplayName = oldStatus.getDisplayName();
        String newStatusDisplayName = newStatus.getDisplayName();

        String subject = buildSubject(oldStatusDisplayName, newStatusDisplayName);
        String body = buildBody(indicatorName, indicatorDescription, oldStatusDisplayName, newStatusDisplayName, oldValue, newValue);

        List<String> recipientEmails = userRepository.findAllByRole(DIRECTOR_ROLE)
                .stream()
                .map(User::getEmail)
                .distinct()
                .toList();

        log.info("Sending indicator notifications: indicatorName={}, oldStatus={}, newStatus={}, oldValue={}, newValue={}, recipientCount={}",
                indicatorName,
                oldStatus,
                newStatus,
                oldValue,
                newValue,
                recipientEmails.size()
        );

        recipientEmails.forEach(email -> {
            emailService.sendEmail(email, subject, body);
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
                
                Indikatorius: %s
                %s

                Indikatoriaus būsena pasikeitė: %s į %s.
                Indikatoriaus reikšmė pasikeitė: %s į %s.
                """.formatted(indicatorName, indicatorDescription,
                oldStatus, newStatus,
                oldValue, newValue);
    }
}