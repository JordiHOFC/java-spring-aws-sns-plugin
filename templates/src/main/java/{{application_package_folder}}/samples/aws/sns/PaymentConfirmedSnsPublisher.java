package br.com.zup.app1.xxx.samples.aws.sns;

import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import io.awspring.cloud.messaging.core.TopicMessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

import static io.awspring.cloud.messaging.core.TopicMessageChannel.NOTIFICATION_SUBJECT_HEADER;

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

    /**
     * MessagePostProcessor to remove the timestamp header because its data type is
     * resolved incorrectly. Also, it will add the subject header to the message.
     *
     * It's exactly here where the bug happens:
     * - io.awspring.cloud.messaging.core.TopicMessageChannel.java:116
     *
     * Some issues:
     * - https://github.com/spring-attic/spring-cloud-aws/issues/221
     * - https://github.com/localstack/localstack/issues?q=is%3Aissue+is%3Aopen+SNS
     *
     * AWS CLI:
     *  1. Create topic:
     *      aws --endpoint-url=http://localhost:4566 sns create-topic --name=bugTopic
     *  2. Publish message:
     *      aws --endpoint-url=http://localhost:4566 sns publish --topic-arn <topicArn> --message "console: hello world" --message-attributes '{"timestamp": { "DataType":"Number.java.lang.Long","StringValue":"1666374349838" }}'
     */
    static class FixHeadersMessagePostProcessor implements MessagePostProcessor {

        private final String subject;

        public FixHeadersMessagePostProcessor(String subject) {
            this.subject = subject;
        }

        @Override
        public Message<?> postProcessMessage(Message<?> message) {

            Map<String, Object> headersWithSubject = new HashMap<>(message.getHeaders());
            headersWithSubject
                    .putIfAbsent(NOTIFICATION_SUBJECT_HEADER, this.subject);

            return MessageBuilder
                    .createMessage(
                            message.getPayload(),
                            new FixedMessageHeaders(headersWithSubject)
                    );
        }

        static class FixedMessageHeaders extends MessageHeaders {
            protected FixedMessageHeaders(Map<String, Object> headers) {
                super(headers, null, -1L); // setting timestamp=-1L removes the header
            }
        }
    }

}
