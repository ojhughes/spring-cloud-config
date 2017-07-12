package org.springframework.cloud.config.server.ssh;

import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.*;

import static java.lang.String.format;
import static org.springframework.cloud.config.server.ssh.SshPropertyValidator.isSshUri;
import static org.springframework.util.StringUtils.hasText;

/**
 * JSR-303 Cross Field validator that ensures that a {@link SshUriProperties} bean for the constraints:
 * - If host key algo is supported
 *
 * Beans annotated with {@link HostKeyAlgoSupported} and {@link Validated} will have the constraints applied.
 *
 * @author Ollie Hughes
 */
public class HostKeyAlgoSupportedValidator implements ConstraintValidator<HostKeyAlgoSupported, SshUriProperties> {
	private static final String GIT_PROPERTY_PREFIX = "spring.cloud.config.server.git.";
	private final SshPropertyValidator sshPropertyValidator = new SshPropertyValidator();
	private static final Set<String> VALID_HOST_KEY_ALGORITHMS = new LinkedHashSet<>(Arrays.asList(
			"ssh-dss","ssh-rsa","ecdsa-sha2-nistp256","ecdsa-sha2-nistp384","ecdsa-sha2-nistp521"));

	@Override
	public void initialize(HostKeyAlgoSupported constrainAnnotation) {}

	@Override
	public boolean isValid(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		Set<Boolean> validationResults = new HashSet<>();
		List<SshUriProperties> extractedProperties = sshPropertyValidator.extractRepoProperties(sshUriProperties);

		for (SshUriProperties extractedProperty : extractedProperties) {
			if (isSshUri(extractedProperty.getUri())) {
				validationResults.add(isHostKeySpecifiedWhenAlgorithmSet(extractedProperty, context));
			}
		}
		return !validationResults.contains(false);
	}

	protected boolean isHostKeySpecifiedWhenAlgorithmSet(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		if (hasText(sshUriProperties.getHostKeyAlgorithm())
				&& !VALID_HOST_KEY_ALGORITHMS.contains(sshUriProperties.getHostKeyAlgorithm())) {

			context.buildConstraintViolationWithTemplate(
					format("Property '%shostKeyAlgorithm' must be one of %s", GIT_PROPERTY_PREFIX, VALID_HOST_KEY_ALGORITHMS))
					.addConstraintViolation();
			return false;
		}
		return true;
	}

}
