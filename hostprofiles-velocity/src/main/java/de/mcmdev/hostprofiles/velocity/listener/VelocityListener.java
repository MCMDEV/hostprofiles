package de.mcmdev.hostprofiles.velocity.listener;

import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.GameProfileRequestEvent;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.GameProfile;
import de.mcmdev.hostprofiles.common.connection.ConnectionEvent;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import de.mcmdev.hostprofiles.common.connection.PingEvent;
import de.mcmdev.hostprofiles.common.handler.HostHandler;
import de.mcmdev.hostprofiles.common.model.Host;
import de.mcmdev.hostprofiles.common.model.Profile;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.net.InetSocketAddress;
import java.util.UUID;

@RequiredArgsConstructor
public class VelocityListener {

	private final HostHandler hostHandler;
	private final ConnectionHandler connectionHandler;

	@Subscribe
	public void onGameProfileRequest(GameProfileRequestEvent event) {
		String hostname = event.getConnection().getVirtualHost().map(InetSocketAddress::getHostName).orElse(null);
		GameProfile gameProfile = event.getGameProfile();
		UUID uuid = gameProfile.getId();
		String name = gameProfile.getName();
		ConnectionEvent connectionEvent = new ConnectionEvent(hostname, uuid, name);
		boolean changes = connectionHandler.handleLogin(connectionEvent);
		if (!changes) return;
		gameProfile = gameProfile.withId(connectionEvent.getUuid()).withName(connectionEvent.getName());
		if (connectionEvent.getSkinValue() != null) {
			GameProfile.Property property;
			if (connectionEvent.getSkinSignature() != null) {
				property = new GameProfile.Property("textures", connectionEvent.getSkinValue(), connectionEvent.getSkinSignature());
			} else {
				property = new GameProfile.Property("textures", connectionEvent.getSkinValue(), "");
			}
			gameProfile.addProperty(property);
		}
		event.setGameProfile(gameProfile);
	}

	@Subscribe
	public void onLogin(LoginEvent event) {
		Player player = event.getPlayer();
		String hostname = player.getVirtualHost().map(InetSocketAddress::getHostName).orElse(null);
		Host host = hostHandler.getHostByAddress(hostname);
		if (host == null) return;
		Profile profile = hostHandler.getProfileByUuid(host, player.getUniqueId());
		if (profile == null && host.isWhitelisted()) {
			event.setResult(ResultedEvent.ComponentResult.denied(LegacyComponentSerializer.legacyAmpersand().deserialize(host.getDisallowedMessage())));
		}
	}

	@Subscribe
	public void onPing(ProxyPingEvent event) {
		String hostname = event.getConnection().getVirtualHost().map(InetSocketAddress::getHostName).orElse(null);
		PingEvent pingEvent = new PingEvent(hostname);
		connectionHandler.handlePing(pingEvent);
		if (pingEvent.getMotd() != null) {
			event.setPing(event.getPing().asBuilder().description(LegacyComponentSerializer.legacySection().deserialize(pingEvent.getMotd())).build());
		}
	}

}
