package de.mcmdev.hostprofiles.common.handler;

import de.mcmdev.hostprofiles.common.model.Host;
import de.mcmdev.hostprofiles.common.model.Profile;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class HostHandler {

	private final List<Host> hosts;

	public Host getHostByAddress(String address) {
		return hosts.stream()
				.filter(host -> host.getAddress().equals(address))
				.findFirst()
				.orElse(null);
	}

	public Profile getProfileByUuid(Host host, UUID uuid) {
		return host.getProfiles().stream()
				.filter(profile -> profile.getOwner().equals(uuid))
				.findFirst()
				.orElse(null);
	}

}
