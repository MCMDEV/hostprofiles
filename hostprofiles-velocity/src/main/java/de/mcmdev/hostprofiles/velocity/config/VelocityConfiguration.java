package de.mcmdev.hostprofiles.velocity.config;

import de.mcmdev.hostprofiles.common.config.Configuration;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class VelocityConfiguration implements Configuration {

	private final String key;
	private final ConfigurationNode node;

	@Override
	public String getName() {
		return key;
	}

	@Override
	public Set<Configuration> getKeys(String key) {
		return node.node(key).childrenMap().entrySet().stream().map(entry -> new VelocityConfiguration((String) entry.getKey(), entry.getValue())).collect(Collectors.toSet());
	}

	@Override
	public String tryString(String key) {
		return node.node(key).getString();
	}

	@Override
	public boolean getBoolean(String key) {
		return node.node(key).getBoolean();
	}
}
