package de.mcmdev.hostprofiles.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.mcmdev.hostprofiles.common.HostprofilesPlatform;
import de.mcmdev.hostprofiles.common.HostprofilesPlugin;
import de.mcmdev.hostprofiles.common.config.Configuration;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import de.mcmdev.hostprofiles.velocity.command.ReloadprofilesCommand;
import de.mcmdev.hostprofiles.velocity.config.VelocityConfiguration;
import de.mcmdev.hostprofiles.velocity.listener.VelocityListener;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Plugin(
        id = "hostprofiles",
        version = "0.1.1",
        description = "Define hosts to which players can connect to have a different profile.",
        url = "https://github.com/MCMDEV/hostprofiles",
        authors = "MCMDEV"
)
public class VelocityHostprofilesPlugin implements HostprofilesPlugin {

	private final ProxyServer server;
	private final Path dataDirectory;
	private HostprofilesPlatform platform;

	@Inject
	public VelocityHostprofilesPlugin(ProxyServer server, @DataDirectory Path dataDirectory) {
		this.server = server;
		this.dataDirectory = dataDirectory;
	}

	@Subscribe
	public void onProxyInitialization(ProxyInitializeEvent event) {
        this.platform = new HostprofilesPlatform(this);
        this.platform.enable();

        server.getCommandManager().register("reloadprofiles", new ReloadprofilesCommand(this.platform));
    }

	@Subscribe
	public void onProxyShutdown(ProxyShutdownEvent event) {
		this.platform.disable();
	}

	@Override
	public HostprofilesPlatform getPlatform() {
		return platform;
	}

	@Override
	public Configuration loadFile(String name) {
        Path path = dataDirectory.resolve(name);

        if (!Files.exists(path)) {
            try {
                InputStream resourceAsStream = getClass().getResourceAsStream("/" + name);
                Files.copy(resourceAsStream, path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfigurationLoader build = YamlConfigurationLoader.builder()
                .nodeStyle(NodeStyle.FLOW)
                .path(path)
                .build();
        try {
            return new VelocityConfiguration("ROOT", build.load());
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
		return null;
	}

	@Override
	public void listen(ConnectionHandler connectionHandler) {
        server.getEventManager().register(this, new VelocityListener(platform.getHostHandler(), connectionHandler));
	}

	@Override
	public void unlisten(ConnectionHandler connectionHandler) {
		server.getEventManager().unregisterListeners(this);
	}

	@Override
	public void disableSelf() {
		// Not implemented in velocity ;(
	}
}
