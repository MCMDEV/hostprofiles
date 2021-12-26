package de.mcmdev.hostprofiles.common.config;

import de.mcmdev.hostprofiles.common.loader.LoadingException;

public class ConfigurationException extends LoadingException {

	public ConfigurationException(String message) {
		super(message);
	}
}
