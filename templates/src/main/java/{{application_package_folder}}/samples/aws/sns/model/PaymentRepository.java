package br.com.zup.app1.xxx.samples.aws.sns.model;

import br.com.zup.app1.xxx.samples.aws.sns.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tip: Here we can use Spring Data JPA to make our lives easier.
 */
@Validated
@Repository
public class PaymentRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentRepository.class);

    /**
     * Just a simple In-Memory Database
     */
    private final Map<UUID, Payment> DATABASE = new ConcurrentHashMap<>();

    public Payment save(@Valid Payment payment) {
        LOGGER.info(
            "Persisting a new payment into database: {}", payment
        );
        DATABASE.put(payment.getId(), payment);
        return payment;
    }

    public Payment findById(UUID id) {
        return DATABASE.get(id);
    }

    public List<Payment> findAll() {
        return new ArrayList<>(
                DATABASE.values().stream()
                        .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                        .toList()
        );
    }

    public void deleteAll() {
        LOGGER.info(
            "Deleting all payments from database..."
        );
        DATABASE.clear();
    }

    public int count() {
        return DATABASE.size();
    }
}
