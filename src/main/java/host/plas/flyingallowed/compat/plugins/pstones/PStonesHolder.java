package host.plas.flyingallowed.compat.plugins.pstones;

import dev.espi.protectionstones.PSRegion;
import dev.espi.protectionstones.ProtectionStones;
import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PStonesHolder extends ApiHolder<ProtectionStones> {
    public PStonesHolder() {
        super(CompatManager.PS_IDENTIFIER, (v) -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("ProtectionStones");
            if (plugin == null) return null;

            return (ProtectionStones) plugin;
        });
    }

    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        PSRegion region = PSRegion.fromLocation(moveData.getTo());

        if (region != null) {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.lands")) {
                if (isPlayerMemberOf(moveData.getPlayer(), region)) {
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

    public boolean isPlayerMemberOf(Player player, PSRegion region) {
        if (region.getLandlord().equals(player.getUniqueId())) return true;

        return region.getMembers().contains(player.getUniqueId());
    }
}
