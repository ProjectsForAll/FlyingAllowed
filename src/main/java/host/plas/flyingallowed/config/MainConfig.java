package host.plas.flyingallowed.config;

import gg.drak.thebase.storage.resources.flat.simple.SimpleConfiguration;
import host.plas.flyingallowed.FlyingAllowed;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class MainConfig extends SimpleConfiguration {
    public MainConfig() {
        super("config.yml", FlyingAllowed.getInstance(), true);
    }

    @Override
    public void init() {
        getAutoToggleOnEnabled();
        getAutoToggleOnBypassPerm();

        getAutoToggleOffEnabled();
        getAutoToggleOffBypassPerm();
        getAutoToggleOffTeleportCheckIfOnGround();
        getAutoToggleOffTeleportMakeInvulnerableTicks();

        getLandsToggleOnPerm();
        getSoftBypassingPerm();
        getCurrentSoftBypassingPlayers();

        getFlyInClaimsPermission();
        getFlyInRegionsPermission();

        getGPClaimExtentsAllowed();
        getHCClaimExtentsAllowed();

        isWGDisableOwnerCheck();
    }

    public boolean getAutoToggleOnEnabled() {
        reloadResource();

        return getOrSetDefault("toggle.on.auto.enabled", true);
    }

    public String getAutoToggleOnBypassPerm() {
        reloadResource();

        return getOrSetDefault("toggle.on.auto.bypass-perm", "flyingallowed.bypass.toggle-on");
    }

    public boolean getAutoToggleOffEnabled() {
        reloadResource();

        return getOrSetDefault("toggle.off.auto.enabled", true);
    }

    public boolean getAutoToggleOffTeleportCheckIfOnGround() {
        reloadResource();

        return getOrSetDefault("toggle.off.auto.teleport.check-if-on-ground", true);
    }

    public long getAutoToggleOffTeleportMakeInvulnerableTicks() {
        reloadResource();

        return getOrSetDefault("toggle.off.auto.teleport.make-invulnerable", 10);
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

    public String getFlyInClaimsPermission() {
        reloadResource();

        return getOrSetDefault("general.flying.in-claims.permission", "flyingallowed.in.claims");
    }

    public String getFlyInClaimsAllPermission() {
        reloadResource();

        return getOrSetDefault("general.flying.in-claims.permission", "flyingallowed.in.claims.all");
    }

    public String getFlyInRegionsPermission() {
        reloadResource();

        return getOrSetDefault("general.flying.in-regions.permission", "flyingallowed.in.regions");
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

    public static List<String> getGPClaimExtentsAllowedDefault() {
        return new ArrayList<>(List.of("Access", "Build"));
    }

    public ConcurrentSkipListSet<String> getGPClaimExtentsAllowed() {
        reloadResource();

        return new ConcurrentSkipListSet<>(getOrSetDefault("specific.grief-prevention.claim-extents.allowed", getGPClaimExtentsAllowedDefault()));
    }

    public static List<String> getHCClaimExtentsAllowedDefault() {
        return new ArrayList<>(List.of("BLOCK_PLACE"));
    }

    public ConcurrentSkipListSet<String> getHCClaimExtentsAllowed() {
        reloadResource();

        return new ConcurrentSkipListSet<>(getOrSetDefault("specific.husk-claims.claim-extents.allowed", getHCClaimExtentsAllowedDefault()));
    }

    public boolean isWGDisableOwnerCheck() {
        reloadResource();

        return getOrSetDefault("specific.disable.owner-check", true);
    }
}
