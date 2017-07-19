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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data container for property based SSH config
 *
 * @author Ollie Hughes
 */
@ConfigurationProperties("spring.cloud.config.server.git")
@Validated
@PrivateKeyIsValid
@HostKeyAndAlgoBothExist
@HostKeyAlgoSupported
public class SshUriProperties {
	private String privateKey;
	private String uri;
	private String hostKeyAlgorithm;
	private String hostKey;
	private boolean ignoreLocalSshSettings;
	private boolean strictHostKeyChecking = true;

	private Map<String, SshUriProperties.SshUriNestedRepoProperties> repos = new LinkedHashMap<>();

	public SshUriProperties(String uri, String hostKeyAlgorithm, String hostKey, String privateKey, boolean ignoreLocalSshSettings, boolean strictHostKeyChecking, Map<String, SshUriNestedRepoProperties> repos) {
		this.uri = uri;
		this.hostKeyAlgorithm = hostKeyAlgorithm;
		this.hostKey = hostKey;
		this.privateKey = privateKey;
		this.ignoreLocalSshSettings = ignoreLocalSshSettings;
		this.strictHostKeyChecking = strictHostKeyChecking;
		this.repos = repos;
	}

	public SshUriProperties() {
	}

	public static SshUriPropertiesBuilder builder() {
		return new SshUriPropertiesBuilder();
	}

	public String getUri() {
		return this.uri;
	}

	public String getHostKeyAlgorithm() {
		return this.hostKeyAlgorithm;
	}

	public String getHostKey() {
		return this.hostKey;
	}

	public String getPrivateKey() {
		return this.privateKey;
	}

	public boolean isIgnoreLocalSshSettings() {
		return this.ignoreLocalSshSettings;
	}

	public boolean isStrictHostKeyChecking() {
		return this.strictHostKeyChecking;
	}

	public Map<String, SshUriProperties.SshUriNestedRepoProperties> getRepos() {
		return this.repos;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setHostKeyAlgorithm(String hostKeyAlgorithm) {
		this.hostKeyAlgorithm = hostKeyAlgorithm;
	}

	public void setHostKey(String hostKey) {
		this.hostKey = hostKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public void setIgnoreLocalSshSettings(boolean ignoreLocalSshSettings) {
		this.ignoreLocalSshSettings = ignoreLocalSshSettings;
	}

	public void setStrictHostKeyChecking(boolean strictHostKeyChecking) {
		this.strictHostKeyChecking = strictHostKeyChecking;
	}

	public void addRepo(String repoName, SshUriProperties.SshUriNestedRepoProperties properties) {
		this.repos.put(repoName, properties);
	}

	public String toString() {
		return "org.springframework.cloud.config.server.ssh.SshUriProperties(uri=" + this.getUri() + " hostKeyAlgorithm=" + this.getHostKeyAlgorithm() + ", hostKey=" + this.getHostKey() + ", privateKey=" + this.getPrivateKey() + ", ignoreLocalSshSettings=" + this.isIgnoreLocalSshSettings() + ", strictHostKeyChecking=" + this.isStrictHostKeyChecking() + ", repos=" + this.getRepos() + ")";
	}

	public static class SshUriPropertiesBuilder {
		private String uri;
		private String hostKeyAlgorithm;
		private String hostKey;
		private String privateKey;
		private boolean ignoreLocalSshSettings;
		private boolean strictHostKeyChecking = true;
		private Map<String, SshUriProperties.SshUriNestedRepoProperties> repos = new LinkedHashMap<>();

		SshUriPropertiesBuilder() {
		}

		public SshUriProperties.SshUriPropertiesBuilder uri(String uri) {
			this.uri = uri;
			return this;
		}

		public SshUriProperties.SshUriPropertiesBuilder hostKeyAlgorithm(String hostKeyAlgorithm) {
			this.hostKeyAlgorithm = hostKeyAlgorithm;
			return this;
		}

		public SshUriProperties.SshUriPropertiesBuilder hostKey(String hostKey) {
			this.hostKey = hostKey;
			return this;
		}

		public SshUriProperties.SshUriPropertiesBuilder privateKey(String privateKey) {
			this.privateKey = privateKey;
			return this;
		}

		public SshUriProperties.SshUriPropertiesBuilder ignoreLocalSshSettings(boolean ignoreLocalSshSettings) {
			this.ignoreLocalSshSettings = ignoreLocalSshSettings;
			return this;
		}

		public SshUriProperties.SshUriPropertiesBuilder strictHostKeyChecking(boolean strictHostKeyChecking) {
			this.strictHostKeyChecking = strictHostKeyChecking;
			return this;
		}

		public SshUriProperties.SshUriPropertiesBuilder repos(Map<String, SshUriProperties.SshUriNestedRepoProperties> repos) {
			this.repos = repos;
			return this;
		}

		public SshUriProperties build() {
			return new SshUriProperties(uri, hostKeyAlgorithm, hostKey, privateKey, ignoreLocalSshSettings, strictHostKeyChecking, repos);
		}

		public SshUriProperties buildAsNestedRepo() {
			return new SshUriProperties.SshUriNestedRepoProperties(uri, hostKeyAlgorithm, hostKey, privateKey, ignoreLocalSshSettings, strictHostKeyChecking, repos);
		}
		public String toString() {
			return "org.springframework.cloud.config.server.ssh.SshUriProperties.SshUriPropertiesBuilder(uri=" + this.uri + "hostKeyAlgorithm=" + this.hostKeyAlgorithm + ", hostKey=" + this.hostKey + ", privateKey=" + this.privateKey + ", ignoreLocalSshSettings=" + this.ignoreLocalSshSettings + ", strictHostKeyChecking=" + this.strictHostKeyChecking + ", repos=" + this.repos + ")";
		}
	}

	/**
	 * Differentiate between sets of properties that are defined in nested Git repos.
	 * This is to prevent boot from
	 */
	public static class SshUriNestedRepoProperties extends SshUriProperties {

		public SshUriNestedRepoProperties(String uri, String hostKeyAlgorithm, String hostKey, String privateKey, boolean ignoreLocalSshSettings, boolean strictHostKeyChecking, Map<String, SshUriNestedRepoProperties> repos) {
			super(uri, hostKeyAlgorithm, hostKey, privateKey, ignoreLocalSshSettings, strictHostKeyChecking, repos);
		}
	}
}
