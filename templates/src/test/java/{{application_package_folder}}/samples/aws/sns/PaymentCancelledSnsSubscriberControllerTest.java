package {{application_package}}.samples.aws.sns;

import {{application_package}}.samples.aws.sns.base.SnsIntegrationTest;
import {{application_package}}.samples.aws.sns.model.Payment;
import {{application_package}}.samples.aws.sns.model.PaymentRepository;
import {{application_package}}.samples.aws.sns.model.PaymentStatus;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class PaymentCancelledSnsSubscriberControllerTest extends SnsIntegrationTest {

    @Autowired
    private NotificationMessagingTemplate messagingTemplate;

    @Autowired
    private AmazonSNS SNS;

    @Autowired
    private PaymentRepository repository;

    private CreateTopicResult CREATED_TOPIC;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
        // creates a test-only topic and subscribes our http endpoint to it
        CREATED_TOPIC = SNS.createTopic("testResultTopic");
        SNS.subscribe(new SubscribeRequest(
                        CREATED_TOPIC.getTopicArn(),
                        "http",
                        getLocalServerDockerEndpointFor("/payments/cancelled/topic-subscriber")
                )
        );
    }

    @AfterEach
    public void cleanUp() {
        // don't forget to clean up the topic
        SNS.deleteTopic(CREATED_TOPIC.getTopicArn());
    }

    @Test
    @DisplayName("should receive a payment-cancelled event from a SNS topic and cancel the payment")
    public void t1() {
        // scenario
        Payment payment = repository.save(new Payment(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(1),
                PaymentStatus.PENDING)
        );

        String subject = "Ecommerce-Site";
        PaymentCancelledEvent event = new PaymentCancelledEvent(payment.getId(), LocalDateTime.now());

        // action
        messagingTemplate
                .sendNotification("testResultTopic", event, subject);

        // validation
        await().atMost(3, SECONDS).untilAsserted(() -> {
            assertThat(repository.findById(event.id()))
                    .extracting(Payment::getStatus)
                        .isEqualTo(PaymentStatus.CANCELLED);
        });
    }

}