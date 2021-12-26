package de.mcmdev.hostprofiles.common.connection;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ConnectionEvent {

	private final String hostname;
	private UUID uuid;
	private String name;
	private String textureValue;
	private String textureSignature;
	private boolean disallowed;
	private String kickMessage;

	public ConnectionEvent(String hostname, UUID uuid, String name) {
		this.hostname = hostname;
		this.uuid = uuid;
		this.name = name;
	}

	public void disallow(String kickMessage) {
		this.disallowed = true;
		this.kickMessage = kickMessage;
	}

}
