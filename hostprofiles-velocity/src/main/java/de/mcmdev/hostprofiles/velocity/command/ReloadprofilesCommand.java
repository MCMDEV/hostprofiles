package de.mcmdev.hostprofiles.velocity.command;

import com.velocitypowered.api.command.RawCommand;
import de.mcmdev.hostprofiles.common.HostprofilesPlatform;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@RequiredArgsConstructor
public class ReloadprofilesCommand implements RawCommand {

    private final HostprofilesPlatform platform;

    @Override
    public void execute(Invocation invocation) {
        platform.reloadConfig();
        invocation.source().sendMessage(Component.text("Configuration reloaded!", NamedTextColor.GREEN));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("hostprofiles.reloadprofiles");
    }
}
