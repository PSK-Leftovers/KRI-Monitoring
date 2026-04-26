package com.leftovers.kri.exception;

import com.leftovers.kri.logging.RequestIdFilter;
import org.slf4j.MDC;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public record ApiError(int status, String message, String requestId, Instant timestamp) {

    public static ApiError of(int status, String message) {
        return new ApiError(status, message, MDC.get(RequestIdFilter.MDC_KEY), Instant.now().truncatedTo(ChronoUnit.MILLIS));
    }
}
