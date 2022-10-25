package {{application_package}}.samples.aws.sns;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCancelledEvent(
        @NotNull UUID id,
        @NotNull @Past LocalDateTime createdAt
) {

}
