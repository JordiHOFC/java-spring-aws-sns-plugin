package {{application_package}}.samples.aws.sns;

import com.amazonaws.services.sns.AmazonSNS;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.autoconfigure.messaging.SnsProperties;
import io.awspring.cloud.core.env.ResourceIdResolver;
import io.awspring.cloud.messaging.core.NotificationMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

@Configuration
public class SnsConfig {

    @Autowired
    private SnsProperties snsProperties;

    @Bean
    public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSns,
                                                                       @Autowired(required = false) ResourceIdResolver resourceIdResolver,
                                                                       ObjectMapper objectMapper) {

        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setPrettyPrint(true);
        converter.setObjectMapper(objectMapper);
        converter.setSerializedPayloadClass(String.class);

        return new NotificationMessagingTemplate(
                amazonSns,
                resourceIdResolver,
                converter
        );
    }

}
