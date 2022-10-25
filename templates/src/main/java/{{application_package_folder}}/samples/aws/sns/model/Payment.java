package {{application_package}}.samples.aws.sns.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static {{application_package}}.samples.aws.sns.model.PaymentStatus.*;

public class Payment {

    @NotNull
    private final UUID id;

    @NotNull
    @Past
    private final LocalDateTime createdAt;

    @NotNull
    private PaymentStatus status;

    @Past
    private LocalDateTime cancelledAt;


    /**
     * Tip: Although optional, we can give some tips about the constraints of
     * constructor's arguments using Bean Validation's annotations.
     */
    public Payment(@NotNull UUID id,
                   @NotNull @Past LocalDateTime createdAt,
                   @NotNull PaymentStatus status) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id.equals(payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", status=" + status +
                ", cancelledAt=" + cancelledAt +
                '}';
    }

    /**
     * Cancels this payment
     */
    public void cancel() {
        this.status = CANCELLED;
        this.cancelledAt = LocalDateTime.now();
    }

}
