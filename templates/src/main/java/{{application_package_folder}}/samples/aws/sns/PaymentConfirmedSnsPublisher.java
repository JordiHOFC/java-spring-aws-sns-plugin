package {{application_package}}.samples.aws.sns;

import {{application_package}}.samples.aws.sns.utils.FixHeadersMessagePostProcessor;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Service
@Validated
public class PaymentConfirmedSnsPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentConfirmedSnsPublisher.class);

    private final String topicName;
    private final NotificationMessagingTemplate messagingTemplate;

    public PaymentConfirmedSnsPublisher(NotificationMessagingTemplate messagingTemplate,
                                        @Value("${samples.aws.sns.publisher-topic}") String topicName) {
        this.topicName = topicName;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Publishes a payment-confirmed event to SNS topic
     */
    public void publish(@Valid PaymentConfirmedEvent event, String subject) {

        /**
         * Tip: you can write your business logic here before sending the event to SNS
         */
        if (event == null) {
            throw new IllegalArgumentException("event can not be null");
        }

        LOGGER.info("Sending a notification from {} to SNS topic {}", subject, event);
        messagingTemplate
                .convertAndSend(topicName, event, new FixHeadersMessagePostProcessor(subject));
    }

}
