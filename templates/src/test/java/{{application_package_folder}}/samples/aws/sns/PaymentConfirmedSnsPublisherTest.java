package {{application_package}}.samples.aws.sns;

import {{application_package}}.samples.aws.sns.base.SnsIntegrationTest;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.List.of;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentConfirmedSnsPublisherTest extends SnsIntegrationTest {

    @Autowired
    private AmazonSNS SNS;
    @Autowired
    private AmazonSQS SQS;

    @Value("${samples.aws.sns.publisher-topic}")
    private String topicName;

    @Autowired
    private PaymentConfirmedSnsPublisher paymentConfirmedSnsPublisher;

    private CreateTopicResult CREATED_TOPIC;
    private CreateQueueResult CREATED_QUEUE;

    @BeforeEach
    public void setUp() {
        // creates the production topic...
        CREATED_TOPIC = SNS.createTopic(topicName);
        // ...and subscribes to it using a test-only SQS queue
        CREATED_QUEUE = SQS.createQueue("testResultQueue");
        SNS.subscribe(new SubscribeRequest(
                        CREATED_TOPIC.getTopicArn(),
                        "sqs",
                        CREATED_QUEUE.getQueueUrl()
                )
        );
    }

    @AfterEach
    public void cleanUp() {
        // don't forget to clean up both topic and queue
        SQS.deleteQueue(CREATED_QUEUE.getQueueUrl());
        SNS.deleteTopic(CREATED_TOPIC.getTopicArn());
    }

    @Test
    @DisplayName("should publish a payment confirmed event into SNS topic")
    public void t1() {
        // scenario
        String subject = "Payment-Gateway";
        PaymentConfirmedEvent event = new PaymentConfirmedEvent(
                UUID.randomUUID(),
                "Credit card payment",
                1042.99,
                LocalDateTime.now()
        );

        // action
        paymentConfirmedSnsPublisher.publish(event, subject);

        // validation
        await().atMost(3, SECONDS).untilAsserted(() -> {
            assertThat(numberOfMessagesInQueue()).isEqualTo(1);
        });
    }

    @Test
    @DisplayName("should not publish a payment confirmed event into SNS topic when message is null")
    public void t2() {

        // action
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            paymentConfirmedSnsPublisher.publish(null, "subject");
        });

        // validation
        assertThat(exception)
                .hasMessage("event can not be null");

        // ...and verify any side-effects if needed
        await().atMost(3, SECONDS).untilAsserted(() -> {
            assertThat(numberOfMessagesInQueue()).isEqualTo(0);
        });
    }

    @Test
    @DefaultLocale("en_US")
    @DisplayName("should not publish a payment confirmed event into SNS topic when message is invalid")
    public void t3() {
        // scenario
        String subject = "payment-app";
        PaymentConfirmedEvent invalidEvent = new PaymentConfirmedEvent(
                UUID.randomUUID(),
                "",
                -0.01,
                LocalDateTime.now()
        );

        // action
        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            paymentConfirmedSnsPublisher.publish(invalidEvent, subject);
        });

        // validation
        assertThat(exception)
                .hasMessageContainingAll(
                        "event.description: must not be empty",
                        "event.amount: must be greater than or equal to 0"
                );

        // ...and verify any side-effects if needed
        await().atMost(3, SECONDS).untilAsserted(() -> {
            assertThat(numberOfMessagesInQueue()).isEqualTo(0);
        });
    }

    private Integer numberOfMessagesInQueue() {
        GetQueueAttributesResult attributes = SQS
                .getQueueAttributes(CREATED_QUEUE.getQueueUrl(), of("All"));

        return Integer.parseInt(
                attributes.getAttributes().get("ApproximateNumberOfMessages")
        );
    }

}