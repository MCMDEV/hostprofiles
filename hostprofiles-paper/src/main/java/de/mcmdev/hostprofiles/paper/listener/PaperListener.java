package de.mcmdev.hostprofiles.paper.listener;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import de.mcmdev.hostprofiles.common.connection.ConnectionEvent;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import de.mcmdev.hostprofiles.common.connection.PingEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.lang.reflect.Field;
import java.util.UUID;

@RequiredArgsConstructor
public class PaperListener implements Listener {

	private static Field GAME_PROFILE_FIELD = null;

	static {
		for (Field field : ServerLoginPacketListenerImpl.class.getDeclaredFields()) {
			if (field.getType().equals(GameProfile.class)) {
				GAME_PROFILE_FIELD = field;
				break;
			}
		}
		GAME_PROFILE_FIELD.setAccessible(true);
	}

	private final ConnectionHandler connectionHandler;

	@EventHandler
	private void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
		String hostname;
		try {
			hostname = getHostname(event.getUniqueId());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return;
		}

		ConnectionEvent connectionEvent = fromBukkit(hostname, event);
		boolean changes = connectionHandler.handleLogin(connectionEvent);
		if (!changes) return;

		if (connectionEvent.isDisallowed()) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			event.setKickMessage(connectionEvent.getKickMessage());
			return;
		}

		PlayerProfile profile = Bukkit.createProfile(connectionEvent.getUuid(), connectionEvent.getName());
		if (connectionEvent.getTextureValue() != null) {
			ProfileProperty property;
			if (connectionEvent.getTextureSignature() != null) {
				property = new ProfileProperty("textures", connectionEvent.getTextureValue(), connectionEvent.getTextureSignature());
			} else {
				property = new ProfileProperty("textures", connectionEvent.getTextureValue());
			}
			profile.setProperty(property);
		}
		event.setPlayerProfile(profile);
	}

	@EventHandler
	public void onPing(PaperServerListPingEvent event) {
		String hostname = event.getClient().getVirtualHost().getHostName();
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

	// I really, really hate this, but I couldn't find a better solution.
	private String getHostname(UUID uuid) throws IllegalAccessException {
		int tries = 0;
		while (tries < 5) {
			for (Connection connection : ((CraftServer) Bukkit.getServer()).getHandle().getServer().getConnection().getConnections()) {
				if (connection.getPacketListener() instanceof ServerLoginPacketListenerImpl impl) {
					GameProfile gameProfile = (GameProfile) GAME_PROFILE_FIELD.get(impl);
					if (gameProfile.getId().equals(uuid)) {
						return impl.hostname.split(":")[0];
					}
				}
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tries++;
		}
		return null;
	}

}
