package de.mcmdev.hostprofiles.paper.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import de.mcmdev.hostprofiles.common.connection.ConnectionEvent;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import de.mcmdev.hostprofiles.common.connection.PingEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.net.InetSocketAddress;

@RequiredArgsConstructor
public class PaperListener implements Listener {

	private final ConnectionHandler connectionHandler;

	@EventHandler(priority = EventPriority.LOWEST)
	private void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
		String hostname = event.getHostname();

		ConnectionEvent connectionEvent = fromBukkit(hostname, event);
		boolean changes = connectionHandler.handleLogin(connectionEvent);
		if (!changes) return;

		if (connectionEvent.isDisallowed()) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			event.setKickMessage(connectionEvent.getKickMessage());
			return;
		}

		PlayerProfile profile = Bukkit.createProfile(connectionEvent.getUuid(), connectionEvent.getName());
		if (connectionEvent.isSkinCopy()) {
			PlayerProfile oldProfile = event.getPlayerProfile();
			oldProfile.complete();
			profile.setProperties(oldProfile.getProperties());
		} else {
			if (connectionEvent.getSkinValue() != null) {
				ProfileProperty property;
				if (connectionEvent.getSkinSignature() != null) {
					property = new ProfileProperty("textures", connectionEvent.getSkinValue(), connectionEvent.getSkinSignature());
				} else {
					property = new ProfileProperty("textures", connectionEvent.getSkinValue());
				}
				profile.setProperty(property);
			}
		}
		event.setPlayerProfile(profile);
	}

	@EventHandler
	public void onPing(PaperServerListPingEvent event) {
		InetSocketAddress virtualHost = event.getClient().getVirtualHost();
		if (virtualHost == null) return;
		String hostname = virtualHost.getHostName();
		PingEvent pingEvent = new PingEvent(hostname);
		connectionHandler.handlePing(pingEvent);
		if (pingEvent.getMotd() != null) {
			event.motd(LegacyComponentSerializer.legacyAmpersand().deserialize(pingEvent.getMotd()));
		}
	}

	private ConnectionEvent fromBukkit(String hostname, AsyncPlayerPreLoginEvent bukkitEvent) {
		return new ConnectionEvent(
				hostname,
				bukkitEvent.getUniqueId(),
				bukkitEvent.getName()
		);
	}

}
