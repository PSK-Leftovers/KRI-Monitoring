package com.leftovers.kri.logging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or class for audit logging via {@link BusinessAuditAspect}.
 * When placed on a class, all its public methods are audited.
 *
 * <pre>
 * // Method-level — explicit action label:
 * {@literal @}Audited(action = "CREATE_INDICATOR")
 * public IndicatorResponse createIndicator(CreateIndicatorRequest req) { ... }
 *
 * // Class-level — all methods use ClassName.methodName as the action:
 * {@literal @}Audited
 * public class IndicatorService { ... }
 * </pre>
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
    /** Human-readable action label. Defaults to {@code ClassName.methodName} when empty. */
    String action() default "";
}
