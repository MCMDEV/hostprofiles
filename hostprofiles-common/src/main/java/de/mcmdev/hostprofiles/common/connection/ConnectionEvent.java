package de.mcmdev.hostprofiles.common.connection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
public class ConnectionEvent {

	private final String hostname;
	private final boolean uuidChanged;
	private UUID uuid;
	private String name;
	private boolean skinCopy;
	private String skinValue;
	private String skinSignature;
	private boolean disallowed;
	private String kickMessage;

	public ConnectionEvent(String hostname, UUID uuid, String name) {
		this.uuidChanged = false;
		this.hostname = hostname.split(":")[0];
		this.uuid = uuid;
		this.name = name;
	}

	public ConnectionEvent(boolean uuidChanged, String hostname, UUID uuid, String name) {
		this.uuidChanged = uuidChanged;
		this.hostname = hostname;
		this.uuid = uuid;
		this.name = name;
	}

	public void disallow(String kickMessage) {
		this.disallowed = true;
		this.kickMessage = kickMessage;
	}

}
