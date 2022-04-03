package de.mcmdev.hostprofiles.common.connection;

import de.mcmdev.hostprofiles.common.handler.HostHandler;
import de.mcmdev.hostprofiles.common.model.Host;
import de.mcmdev.hostprofiles.common.model.Profile;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConnectionHandler {

	private final HostHandler hostHandler;

	public boolean handleLogin(ConnectionEvent connectionEvent) {
		Host host = hostHandler.getHostByAddress(connectionEvent.getHostname());
		if (host == null) return false;
		Profile profile = hostHandler.getProfileByOwner(host, connectionEvent.getUuid());
		if (profile == null) {
			if (host.isWhitelisted()) {
				connectionEvent.disallow(host.getDisallowedMessage());
			}
			return true;
		}
		connectionEvent.setUuid(profile.getUuid());
		connectionEvent.setName(profile.getName());
		connectionEvent.setSkinCopy(profile.isSkinCopy());
		if (connectionEvent.getSkinValue() != null) {
			connectionEvent.setSkinValue(profile.getSkinValue());
			if (connectionEvent.getSkinSignature() != null) {
				connectionEvent.setSkinSignature(profile.getSkinSignature());
			}
		}
		return true;
	}

	public void handlePing(PingEvent pingEvent) {
		Host host = hostHandler.getHostByAddress(pingEvent.getHostname());
		if (host == null) return;
		if (host.getMotd() != null) {
			pingEvent.setMotd(host.getMotd());
		}
	}

}
