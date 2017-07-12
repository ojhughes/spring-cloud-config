/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.config.server.ssh;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jcraft.jsch.JSch;
import org.eclipse.jgit.transport.URIish;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import static org.springframework.util.StringUtils.hasText;

/**
 * Validate SSH related properties
 *
 * @author Ollie Hughes
 */
@Component
@EnableConfigurationProperties(SshUriProperties.class)
public class SshPropertyValidator {

	private static final Set<String> VALID_HOST_KEY_ALGORITHMS = new LinkedHashSet<>(Arrays.asList(
			"ssh-dss","ssh-rsa","ecdsa-sha2-nistp256","ecdsa-sha2-nistp384","ecdsa-sha2-nistp521"));
	private static final String GIT_PROPERTY_PREFIX = "spring.cloud.config.server.git.";

	static boolean isSshUri(Object uri) {
		if(uri != null) {
			try {
				URIish urIish = new URIish(uri.toString());
				String scheme = urIish.getScheme();
				if(scheme == null && hasText(urIish.getHost()) && hasText(urIish.getUser())) {
					//JGit returns null if using SCP URI but user and host will be populated
					return true;
				}
				return scheme != null && !scheme.matches("^(http|https)$");

			} catch (URISyntaxException e) {
				return false;
			}
		}
		return false;
	}

	protected List<SshUriProperties> extractRepoProperties(SshUriProperties sshUriProperties) {
		List<SshUriProperties> allRepoProperties = new ArrayList<>();
		allRepoProperties.add(sshUriProperties);
		Map<String, SshUriProperties> repos = sshUriProperties.getRepos();
		if (repos != null) {
			allRepoProperties.addAll(repos.values());
		}
		return allRepoProperties;
	}
}
