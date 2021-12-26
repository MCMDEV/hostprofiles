package de.mcmdev.hostprofiles.common;

import de.mcmdev.hostprofiles.common.config.Configuration;
import de.mcmdev.hostprofiles.common.config.ConfigurationHostprofileLoader;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import de.mcmdev.hostprofiles.common.handler.HostHandler;
import de.mcmdev.hostprofiles.common.loader.HostprofileLoader;
import de.mcmdev.hostprofiles.common.loader.LoadingException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HostprofilesPlatform {

	private final HostprofilesPlugin plugin;
	private Configuration configuration;
	private HostprofileLoader loader;
	private HostHandler hostHandler;
	private ConnectionHandler connectionHandler;

	public void enable() {
		this.configuration = plugin.loadFile("config.yml");
		this.loader = new ConfigurationHostprofileLoader(this.configuration);
		try {
			this.hostHandler = new HostHandler(this.loader.load());
		} catch (LoadingException e) {
			e.printStackTrace();
			plugin.disableSelf();
		}
		this.connectionHandler = new ConnectionHandler(this.hostHandler);
		plugin.listen(this.connectionHandler);
	}

	public void disable() {
		this.configuration = null;
		this.loader = null;
		this.hostHandler = null;
		plugin.unlisten(connectionHandler);
		this.connectionHandler = null;
	}

}
