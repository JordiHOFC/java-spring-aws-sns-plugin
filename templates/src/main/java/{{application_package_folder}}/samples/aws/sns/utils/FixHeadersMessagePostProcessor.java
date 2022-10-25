package br.com.zup.edu.app2.xxx.samples.aws.sns.utils;

import br.com.zup.edu.app2.xxx.samples.aws.sns.PaymentConfirmedSnsPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.core.MessagePostProcessor;
import org.springframework.messaging.support.MessageBuilder;

import java.util.HashMap;
import java.util.Map;

import static io.awspring.cloud.messaging.core.TopicMessageChannel.NOTIFICATION_SUBJECT_HEADER;

/**
 * MessagePostProcessor to remove the timestamp header because its data type is
 * resolved incorrectly. Also, it will add the subject header to the message.
 * <p>
 * It's exactly here where the bug happens:
 * - io.awspring.cloud.messaging.core.TopicMessageChannel.java:116
 * <p>
 * Some issues:
 * - https://github.com/spring-attic/spring-cloud-aws/issues/221
 * - https://github.com/localstack/localstack/issues?q=is%3Aissue+is%3Aopen+SNS
 * <p>
 * AWS CLI:
 * 1. Create topic:
 * aws --endpoint-url=http://localhost:4566 sns create-topic --name=bugTopic
 * 2. Publish message:
 * aws --endpoint-url=http://localhost:4566 sns publish --topic-arn <topicArn> --message "console: hello world" --message-attributes '{"timestamp": { "DataType":"Number.java.lang.Long","StringValue":"1666374349838" }}'
 */
public class FixHeadersMessagePostProcessor implements MessagePostProcessor {

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
