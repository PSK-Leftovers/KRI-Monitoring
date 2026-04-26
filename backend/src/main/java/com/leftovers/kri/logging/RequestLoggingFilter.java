package com.leftovers.kri.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@Order(2)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String LOG_MESSAGE =
            "Request Completed! method={} path={} status={} duration_ms={}";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        long start = System.nanoTime();
        Throwable throwable = null;

        try {
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException | RuntimeException ex) {
            throwable = ex;
            throw ex;
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            HttpStatus httpStatus = resolveHttpStatus(response.getStatus());

            logRequestCompleted(request, httpStatus, durationMs, throwable);
        }
    }

    private void logRequestCompleted(
            HttpServletRequest request,
            HttpStatus httpStatus,
            long durationMs,
            Throwable throwable) {

            if (httpStatus.is5xxServerError()) {
                log.error(LOG_MESSAGE,
                        request.getMethod(),
                        request.getRequestURI(),
                        httpStatus,
                        durationMs,
                        throwable
                );
            } else if (httpStatus.is4xxClientError()) {
                log.warn(LOG_MESSAGE,
                        request.getMethod(),
                        request.getRequestURI(),
                        httpStatus,
                        durationMs,
                        throwable
                );
            } else {
                log.info(LOG_MESSAGE,
                        request.getMethod(),
                        request.getRequestURI(),
                        httpStatus,
                        durationMs,
                        throwable
                );
            }
    }

    private HttpStatus resolveHttpStatus(int status) {
        return Optional.ofNullable(HttpStatus.resolve(status))
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
