package org.springframework.cloud.config.server.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
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
 * - Private key is present and can be correctly parsed using {@link com.jcraft.jsch.KeyPair}
 *
 * Beans annotated with {@link PrivateKeyValidator} and {@link Validated} will have the constraints applied.
 *
 * @author Ollie Hughes
 */
public class PrivateKeyValidator implements ConstraintValidator<PrivateKeyIsValid, SshUriProperties> {
	private static final String GIT_PROPERTY_PREFIX = "spring.cloud.config.server.git.";
	private final SshPropertyValidator sshPropertyValidator = new SshPropertyValidator();

	@Override
	public void initialize(PrivateKeyIsValid constrainAnnotation) {}

	@Override
	public boolean isValid(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		Set<Boolean> validationResults = new HashSet<>();
		List<SshUriProperties> extractedProperties = sshPropertyValidator.extractRepoProperties(sshUriProperties);

		for (SshUriProperties extractedProperty : extractedProperties) {
			if (isSshUri(extractedProperty.getUri())) {
				validationResults.add(
						isPrivateKeyFormatCorrect(extractedProperty, context)
								&& isPrivateKeyPresent(extractedProperty, context));
			}
		}
		return !validationResults.contains(false);

	}

	protected boolean isPrivateKeyPresent(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		if (!hasText(sshUriProperties.getPrivateKey())) {
				context.buildConstraintViolationWithTemplate(
						format("Property '%shostKey' must be set when '%shostKeyAlgorithm' is specified", GIT_PROPERTY_PREFIX, GIT_PROPERTY_PREFIX))
						.addConstraintViolation();
			return false;
		}
		return true;
	}

	protected boolean isPrivateKeyFormatCorrect(SshUriProperties sshUriProperties, ConstraintValidatorContext context) {
		try {
			KeyPair.load(new JSch(), sshUriProperties.getPrivateKey().getBytes(), null);
			return true;
		} catch (JSchException e) {
			context.buildConstraintViolationWithTemplate(
					format("Property '%sprivateKey' contains is not a valid private key", GIT_PROPERTY_PREFIX))
					.addConstraintViolation();
			return false;
		}
	}
}
