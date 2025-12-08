package com.chronos.chronos_engine.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Column; // Added
import lombok.Data;

@Embeddable
@Data
public class JobRetryConfig {

    @Column(name = "max_retries")
    private int maxRetries = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "retry_backoff_strategy", length = 20)
    private Retrybackoff retryBackoffStrategy = Retrybackoff.FIXED;

    @Column(name = "retry_delay_seconds")
    private int retryDelaySeconds = 60;
}
