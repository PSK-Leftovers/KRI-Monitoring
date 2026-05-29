package com.leftovers.kri.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.stream.Collectors;

@Aspect
@Component
@ConditionalOnProperty(name = "audit.enabled", havingValue = "true", matchIfMissing = false)
public class BusinessAuditAspect {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    private String[] resolveIdentity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            String username = auth.getName();
            String roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .collect(Collectors.joining(", "));
            return new String[]{username, roles};
        }
        return new String[]{"anonymous", ""};
    }

    @Around("@annotation(com.leftovers.kri.logging.Audited) || @within(com.leftovers.kri.logging.Audited)")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        String className = pjp.getSignature().getDeclaringType().getSimpleName();
        String methodName = method.getName();

        Audited audited = method.getAnnotation(Audited.class);
        if (audited == null) {
            audited = pjp.getTarget().getClass().getAnnotation(Audited.class);
        }
        String action = (audited != null && !audited.action().isEmpty())
                ? audited.action()
                : className + "." + methodName;

        String timestamp = Instant.now().toString();
        long start = System.currentTimeMillis();
        try {
            Object result = pjp.proceed();
            long duration = System.currentTimeMillis() - start;
            // Read auth after proceed so login() has time to populate the SecurityContext
            String[] identity = resolveIdentity();
            auditLogger.info("timestamp={} user={} roles=\"{}\" action={} class={} method={} durationMs={} status=OK",
                    timestamp, identity[0], identity[1], action, className, methodName, duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            String[] identity = resolveIdentity();
            auditLogger.warn("timestamp={} user={} roles=\"{}\" action={} class={} method={} durationMs={} status=ERROR exception={} errorMessage=\"{}\"",
                    timestamp, identity[0], identity[1], action, className, methodName, duration,
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}
