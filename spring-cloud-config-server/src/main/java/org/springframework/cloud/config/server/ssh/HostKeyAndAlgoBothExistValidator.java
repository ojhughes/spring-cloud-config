package org.springframework.cloud.config.server.ssh;

import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static org.springframework.cloud.config.server.ssh.SshPropertyValidator.isSshUri;
import static org.springframework.util.StringUtils.hasText;

/**
 * JSR-303 Cross Field validator that ensures that a {@link SshUriProperties} bean for the constraints:
 * - If host key is set then host key algo must also be set
 * - If host key algo is set then host key must also be set
 *
 * Beans annotated with {@link HostKeyAndAlgoBothExist} and {@link Validated} will have the constraints applied.
 *
 * @author Ollie Hughes
 */
public class HostKeyAndAlgoBothExistValidator implements ConstraintValidator<HostKeyAndAlgoBothExist, SshUriProperties> {
	private static final String GIT_PROPERTY_PREFIX = "spring.cloud.config.server.git.";
	private final SshPropertyValidator sshPropertyValidator = new SshPropertyValidator();

	@Override
	public void initialize(HostKeyAndAlgoBothExist constrainAnnotation) {}

	@Override
	public boolean isValid(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		Set<Boolean> validationResults = new HashSet<>();
		List<SshUriProperties> extractedProperties = sshPropertyValidator.extractRepoProperties(sshUriProperties);

		for (SshUriProperties extractedProperty : extractedProperties) {
			if (isSshUri(extractedProperty.getUri())) {
				validationResults.add(
						isAlgorithmSpecifiedWhenHostKeySet(extractedProperty, context)
					   && isHostKeySpecifiedWhenAlgorithmSet(extractedProperty, context));
			}
		}
		return !validationResults.contains(false);
	}

	protected boolean isHostKeySpecifiedWhenAlgorithmSet(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		if (hasText(sshUriProperties.getHostKeyAlgorithm()) && !hasText(sshUriProperties.getHostKey())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(
						format("Property '%shostKey' must be set when '%shostKeyAlgorithm' is specified", GIT_PROPERTY_PREFIX, GIT_PROPERTY_PREFIX))
						.addConstraintViolation();
			return false;
		}
		return true;
	}

	protected boolean isAlgorithmSpecifiedWhenHostKeySet(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		if (hasText(sshUriProperties.getHostKey()) && !hasText(sshUriProperties.getHostKeyAlgorithm())) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(
					format("Property '%shostKeyAlgorithm' must be set when '%shostKey' is specified", GIT_PROPERTY_PREFIX, GIT_PROPERTY_PREFIX))
					.addConstraintViolation();
			return false;
		}
		return true;
	}
}
