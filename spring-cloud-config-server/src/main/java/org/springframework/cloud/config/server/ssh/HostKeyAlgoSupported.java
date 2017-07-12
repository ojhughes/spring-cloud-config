package org.springframework.cloud.config.server.ssh;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ohughes on 7/12/17.
 */
@Constraint(validatedBy = HostKeyAlgoSupportedValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HostKeyAlgoSupported {
	String message() default "{HostKeyAlgoSupported.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}
