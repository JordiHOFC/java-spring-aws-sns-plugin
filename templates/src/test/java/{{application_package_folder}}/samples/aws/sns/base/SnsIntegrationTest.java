package {{application_package}}.samples.aws.sns.base;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SNS;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.SQS;

/**
 * Base class responsible for starting Localstack and configuring it into the application
 * before tests are executed
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers @DirtiesContext
public abstract class SnsIntegrationTest {

    private static DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack");

    @Container
    public static LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
                                                                    .withServices(SNS, SQS);

    /**
     * Just configures Localstack's server endpoints in the application
     */
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        // SNS
        registry.add("cloud.aws.sns.endpoint",
                () -> LOCALSTACK_CONTAINER.getEndpointOverride(SNS).toString());
        // SQS
        registry.add("cloud.aws.sqs.endpoint",
                () -> LOCALSTACK_CONTAINER.getEndpointOverride(SQS).toString());
    }

    @LocalServerPort
    public Integer serverPort;

    /**
     * Returns local server endpoint URI as string.
     */
    public String getLocalServerEndpointFor(String endpoint) {
        return "http://localhost:{port}/{endpoint}"
                .replace("{port}", serverPort.toString())
                .replace("{endpoint}", endpoint.replaceFirst("[/]", ""));
    }

    /**
     * Returns local server endpoint URI as string that's accessible from Docker containers
     */
    public String getLocalServerDockerEndpointFor(String endpoint) {
        return "http://host.docker.internal:{port}/{endpoint}"
                .replace("{port}", serverPort.toString())
                .replace("{endpoint}", endpoint.replaceFirst("[/]", ""));
    }

}
