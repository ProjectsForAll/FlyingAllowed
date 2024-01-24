package host.plas.flyingallowed.config;

import host.plas.flyingallowed.FlyingAllowed;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import tv.quaint.storage.resources.flat.simple.SimpleConfiguration;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", FlyingAllowed.getInstance(), false);
    }

    @Override
    public void init() {
        getAutoToggleOnEnabled();
        getAutoToggleOnBypassPerm();
        getAutoToggleOffEnabled();
        getAutoToggleOffBypassPerm();

        getLandsToggleOnPerm();
    }

    public boolean getAutoToggleOnEnabled() {
        reloadResource();

        return getOrSetDefault("toggle.on.auto.enabled", false);
    }

    public String getAutoToggleOnBypassPerm() {
        reloadResource();

        return getOrSetDefault("toggle.on.auto.bypass-perm", "flyingallowed.bypass.toggle-on");
    }

    public boolean getAutoToggleOffEnabled() {
        reloadResource();

        return getOrSetDefault("toggle.off.auto.enabled", true);
    }

    public String getAutoToggleOffBypassPerm() {
        reloadResource();

        return getOrSetDefault("toggle.off.auto.bypass-perm", "flyingallowed.bypass.toggle-off");
    }

    public String getLandsToggleOnPerm() {
        reloadResource();

        return getOrSetDefault("toggle.on.lands.perm", "flyingallowed.lands.toggle-on");
    }

    public String getSoftBypassingPerm() {
        reloadResource();

        return getOrSetDefault("bypassing.soft.permission", "flyingallowed.bypass.soft");
    }

    public ConcurrentSkipListSet<String> getCurrentSoftBypassingPlayers() {
        reloadResource();

        return new ConcurrentSkipListSet<>(getOrSetDefault("bypassing.soft.player-list", new ArrayList<>()));
    }

    public void removeBypassingPlayer(String uuid) {
        reloadResource();

        ConcurrentSkipListSet<String> bypassingPlayers = getCurrentSoftBypassingPlayers();
        bypassingPlayers.remove(uuid);

        write("bypassing.soft.player-list", bypassingPlayers);
    }

    public void addBypassingPlayer(String uuid) {
        reloadResource();

        ConcurrentSkipListSet<String> bypassingPlayers = getCurrentSoftBypassingPlayers();
        bypassingPlayers.add(uuid);

        write("bypassing.soft.player-list", bypassingPlayers);
    }

    public boolean isBypassing(String uuid) {
        reloadResource();

        return getCurrentSoftBypassingPlayers().contains(uuid);
    }

    public void addBypassingPlayer(Player player) {
        addBypassingPlayer(player.getUniqueId().toString());
    }

    public void removeBypassingPlayer(Player player) {
        removeBypassingPlayer(player.getUniqueId().toString());
    }

    public boolean isBypassing(Player player) {
        return isBypassing(player.getUniqueId().toString());
    }
}
