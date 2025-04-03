package host.plas.flyingallowed.compat.plugins.pstones;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicBoolean;

public class PStonesHolder extends FlyingHolder<ProtectionStones> {
    public PStonesHolder() {
        super(CompatManager.PS_IDENTIFIER, (v) -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtectionStones");
            if (plugin == null) return null;

            return (ProtectionStones) plugin;
        }, FlightExtent.PROTECTION_STONES);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        PSRegion region = PSRegion.fromLocation(moveData.getTo());

        if (region != null) {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.lands")) {
                if (canFlyIn(moveData.getPlayer(), region)) {
                    return FlightAbility.ABLE_TO_FLY;
                } else {
                    return FlightAbility.UNABLE_TO_FLY;
                }
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        } else {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.lands")) {
                return FlightAbility.NO_CLAIM;
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        }
    }

    public boolean canFlyIn(Player player, PSRegion region) {
        return isOwner(player, region) || isMember(player, region) || isLandlord(player, region);
    }

    public static boolean isLandlord(Player player, PSRegion region) {
        return region.getLandlord() != null && region.getLandlord().equals(player.getUniqueId());
    }

    public static boolean isOwner(Player player, PSRegion region) {
        AtomicBoolean isOwner = new AtomicBoolean(false);

        region.getOwners().forEach(uuid -> {
            if (uuid.equals(player.getUniqueId())) {
                isOwner.set(true);
            }
        });

        return isOwner.get();
    }

    public static boolean isMember(Player player, PSRegion region) {
        AtomicBoolean isOwner = new AtomicBoolean(false);

        region.getMembers().forEach(uuid -> {
            if (uuid.equals(player.getUniqueId())) {
                isOwner.set(true);
            }
        });

        return isOwner.get();
    }
}
