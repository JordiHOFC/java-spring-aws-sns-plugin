package {{application_package}}.samples.aws.sns;

import {{application_package}}.samples.aws.sns.model.Payment;
import {{application_package}}.samples.aws.sns.model.PaymentRepository;
import io.awspring.cloud.messaging.config.annotation.NotificationMessage;
import io.awspring.cloud.messaging.config.annotation.NotificationSubject;
import io.awspring.cloud.messaging.endpoint.NotificationStatus;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationMessageMapping;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationSubscriptionMapping;
import io.awspring.cloud.messaging.endpoint.annotation.NotificationUnsubscribeConfirmationMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * We can subscribe a specific HTTP(s) endpoint to SNS topic, so that this endpoint will be notified
 * with messages sent to topic.
 */
@RestController
@RequestMapping("/payments/cancelled/topic-subscriber")
public class PaymentCancelledSnsSubscriberController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentCancelledSnsSubscriberController.class);

    private final PaymentRepository repository;

    public PaymentCancelledSnsSubscriberController(PaymentRepository repository) {
        this.repository = repository;
    }

    @NotificationMessageMapping
    public void handleNotificationMessage(@NotificationSubject String subject,
                                          @NotificationMessage PaymentCancelledEvent message) {
        // handle message
        LOGGER.info(
                "Receiving notification from {} with payload: {}",
                subject, message
        );

        // finds the payment and cancels it
        Payment payment = repository.findById(message.id());
        payment.cancel();

        repository.save(payment);
    }


    @NotificationSubscriptionMapping
    public void handleSubscriptionMessage(NotificationStatus notificationStatus) {
            LOGGER.info("Handling subscription message...");
            notificationStatus
                    .confirmSubscription();
    }

    @NotificationUnsubscribeConfirmationMapping
    public void handleUnsubscribeMessage(NotificationStatus notificationStatus) {
        LOGGER.info("Handling unsubscribe message...");
        notificationStatus
                .confirmSubscription();
    }

}
