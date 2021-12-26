package de.mcmdev.hostprofiles.common;

import de.mcmdev.hostprofiles.common.config.Configuration;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;

public interface HostprofilesPlugin {

	HostprofilesPlatform getPlatform();

	Configuration loadFile(String name);

	void listen(ConnectionHandler connectionHandler);

	void unlisten(ConnectionHandler connectionHandler);

	void disableSelf();
}
