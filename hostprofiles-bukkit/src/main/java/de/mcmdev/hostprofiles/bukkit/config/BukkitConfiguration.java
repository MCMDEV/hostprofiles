package de.mcmdev.hostprofiles.bukkit.config;

import de.mcmdev.hostprofiles.common.config.Configuration;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BukkitConfiguration implements Configuration {

	private final ConfigurationSection handle;

	@Override
	public String getName() {
		return handle.getName();
	}

	@Override
	public Set<Configuration> getKeys(String key) {
		ConfigurationSection section = handle.getConfigurationSection(key);
		return section.getKeys(false)
				.stream()
				.map(s -> new BukkitConfiguration(section.getConfigurationSection(s)))
				.collect(Collectors.toSet());
	}

	@Override
	public String tryString(String key) {
		return handle.getString(key);
	}

	@Override
	public boolean getBoolean(String key) {
		return handle.getBoolean(key);
	}
}
