package de.mcmdev.hostprofiles.common.connection;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PingEvent {

	private final String hostname;
	private String motd;

	public PingEvent(String hostname) {
		this.hostname = hostname;
	}
}
