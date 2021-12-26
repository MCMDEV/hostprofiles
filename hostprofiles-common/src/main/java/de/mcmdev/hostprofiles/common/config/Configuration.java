package de.mcmdev.hostprofiles.common.config;

import java.util.Set;

public interface Configuration {

	String getName();

	Set<Configuration> getKeys(String key);

	String tryString(String key);

	default String getString(String key) throws ConfigurationException {
		String triedString = tryString(key);
		if (triedString == null) {
			throw new ConfigurationException("No value for key " + key + " is present. Please check if you have not deleted any required parts in the configuration.");
		}
		return triedString;
	}

	boolean getBoolean(String key);

}
