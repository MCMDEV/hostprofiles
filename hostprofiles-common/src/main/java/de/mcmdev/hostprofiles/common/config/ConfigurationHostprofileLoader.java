package de.mcmdev.hostprofiles.common.config;

import de.mcmdev.hostprofiles.common.loader.HostprofileLoader;
import de.mcmdev.hostprofiles.common.model.Host;
import de.mcmdev.hostprofiles.common.model.Profile;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ConfigurationHostprofileLoader implements HostprofileLoader {

	private final Configuration configuration;

	@Override
	public List<Host> load() throws ConfigurationException {
		List<Host> hostList = new ArrayList<>();
		for (Configuration hostsSection : configuration.getKeys("hosts")) {
			String hostId = hostsSection.getName();
			String address = hostsSection.getString("address");
			boolean whitelisted = hostsSection.getBoolean("whitelisted");
			String disallowedMessage = hostsSection.getString("disallowedMessage");
			String motd = hostsSection.tryString("motd");
			List<Profile> profileList = new ArrayList<>();
			for (Configuration profilesSection : hostsSection.getKeys("profiles")) {
				String profileId = profilesSection.getName();
				UUID owner = UUID.fromString(profilesSection.getString("owner"));
				UUID uuid = UUID.fromString(profilesSection.getString("uuid"));
				String name = profilesSection.getString("name");
				boolean skinCopy = profilesSection.getBoolean("skin.copy");
				String skinValue = profilesSection.tryString("skin.value");
				String skinSignature = profilesSection.tryString("skin.signature");
				profileList.add(new Profile(profileId, owner, uuid, name, skinCopy, skinValue, skinSignature));
			}
			hostList.add(new Host(hostId, address, whitelisted, disallowedMessage, motd, profileList));
		}
		return hostList;
	}
}
