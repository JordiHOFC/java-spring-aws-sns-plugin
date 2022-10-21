package {{application_package}}.samples.aws.sns;

import {{application_package}}.samples.aws.sns.model.Payment;
import {{application_package}}.samples.aws.sns.model.PaymentStatus;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentConfirmedEvent(
        @NotNull UUID id,
        @NotEmpty @Size(max = 120) String description,
        @NotNull @PositiveOrZero Double amount,
        @NotNull @Past LocalDateTime createdAt
) {

    public Payment toModel() {
        return new Payment(id, createdAt, PaymentStatus.CONFIRMED);
    }
}
