package {{application_package}}.samples.aws.sns;

import {{application_package}}.samples.aws.sns.base.SnsIntegrationTest;
import {{application_package}}.samples.aws.sns.model.PaymentRepository;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
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

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentConfirmedSnsPublisherTest extends SnsIntegrationTest {

    @Autowired
    private PaymentConfirmedSnsPublisher paymentConfirmedSnsPublisher;

    @Autowired
    private PaymentRepository repository;

    @Autowired
    private AmazonSNS SNS;

    @Value("${samples.aws.sns.publisher-topic}")
    private String topicName;

    private CreateTopicResult CREATED_TOPIC;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        CREATED_TOPIC = SNS.createTopic(topicName);
        SNS.subscribe(new SubscribeRequest(
                        CREATED_TOPIC.getTopicArn(),
                        "http",
                        getLocalServerDockerEndpointFor("/payments/confirmed/topic-subscriber")
                )
        );
    }

    @AfterEach
    public void cleanUp() {
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
            assertThat(repository.findAll())
                    .hasSize(1)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactly(event.toModel());
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
    }

}