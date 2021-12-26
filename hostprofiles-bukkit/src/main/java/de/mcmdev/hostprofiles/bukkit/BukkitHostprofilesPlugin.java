package de.mcmdev.hostprofiles.bukkit;

import de.mcmdev.hostprofiles.bukkit.config.BukkitConfiguration;
import de.mcmdev.hostprofiles.bukkit.listener.BukkitListener;
import de.mcmdev.hostprofiles.common.HostprofilesPlatform;
import de.mcmdev.hostprofiles.common.HostprofilesPlugin;
import de.mcmdev.hostprofiles.common.config.Configuration;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class BukkitHostprofilesPlugin extends JavaPlugin implements HostprofilesPlugin {

	private HostprofilesPlatform platform;

	@Override
	public void onEnable() {
		this.platform = new HostprofilesPlatform(this);
		this.platform.enable();
	}

	@Override
	public void onDisable() {
		this.platform.disable();
		this.platform = null;
	}

	@Override
	public HostprofilesPlatform getPlatform() {
		return this.platform;
	}

	@Override
	public Configuration loadFile(String name) {
		File file = new File(getDataFolder(), name);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			saveResource(name, false);
		}

		YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
		return new BukkitConfiguration(yamlConfiguration);
	}

	@Override
	public void listen(ConnectionHandler connectionHandler) {
		Bukkit.getPluginManager().registerEvents(new BukkitListener(connectionHandler), this);
	}

	@Override
	public void unlisten(ConnectionHandler connectionHandler) {
		HandlerList.unregisterAll(this);
	}

	@Override
	public void disableSelf() {
		Bukkit.getPluginManager().disablePlugin(this);
	}


}
