package de.mcmdev.hostprofiles.bukkit.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.mcmdev.hostprofiles.common.connection.ConnectionEvent;
import de.mcmdev.hostprofiles.common.connection.ConnectionHandler;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.lang.reflect.Field;

@RequiredArgsConstructor
public class BukkitListener implements Listener {

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
		GameProfile gameProfile;
		ServerLoginPacketListenerImpl packetListener = null;
		int tries = 0;
		while (tries < 5) {
			try {
				for (Connection connection : ((CraftServer) Bukkit.getServer()).getHandle().getServer().getConnection().getConnections()) {
					if (connection.getPacketListener() instanceof ServerLoginPacketListenerImpl) {
						packetListener = (ServerLoginPacketListenerImpl) connection.getPacketListener();
						gameProfile = (GameProfile) GAME_PROFILE_FIELD.get(packetListener);
						if (gameProfile.getId().equals(event.getUniqueId())) {
							break;
						}
					}
				}
				Thread.sleep(20);
			} catch (InterruptedException | IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
			tries++;
		}

		String hostname = packetListener.hostname.split(":")[0];

		ConnectionEvent connectionEvent = fromBukkit(hostname, event);
		boolean changes = connectionHandler.handleLogin(connectionEvent);
		if (!changes) {
			return;
		}

		if (connectionEvent.isDisallowed()) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
			event.setKickMessage(connectionEvent.getKickMessage());
			return;
		}

		GameProfile newGameProfile = new GameProfile(connectionEvent.getUuid(), connectionEvent.getName());
		if (connectionEvent.getSkinValue() != null) {
			Property property;
			if (connectionEvent.getSkinSignature() != null) {
				property = new Property("textures", connectionEvent.getSkinValue(), connectionEvent.getSkinSignature());
			} else {
				property = new Property("textures", connectionEvent.getSkinValue());
			}
			newGameProfile.getProperties().put("textures", property);
		}
		try {
			GAME_PROFILE_FIELD.set(packetListener, newGameProfile);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
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
