package host.plas.flyingallowed.compat.plugins.fskyblock;

import com.craftaro.skyblock.api.SkyBlockAPI;
import com.craftaro.skyblock.api.island.Island;
import com.craftaro.skyblock.api.island.IslandManager;
import com.craftaro.skyblock.api.island.IslandRole;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class FSkyblockHolder extends FlyingHolder<IslandManager> {
    public FSkyblockHolder() {
        super(FSkyblockHeld.IDENTIFIER, (v) -> {
            // FabledSkyBlock 2.x shares the plugin name but lives under com.songoda;
            // there SkyBlockAPI is absent, the lookup throws, and the hook stays disabled.
            Plugin plugin = Bukkit.getPluginManager().getPlugin(FSkyblockHeld.IDENTIFIER);
            if (plugin == null || ! plugin.isEnabled()) return null;
            return SkyBlockAPI.getIslandManager();
        }, FlightExtent.FABLED_SKYBLOCK);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        Island island = api().getIslandAtLocation(moveData.getTo());

        if (island != null) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.hasFlyInClaimPermission()) {
                if (isPlayerMemberOfIsland(moveData.getPlayer(), island)) {
                    return FlightAbility.ABLE_TO_FLY;
                } else {
                    return FlightAbility.UNABLE_TO_FLY;
                }
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        } else {
            if (moveData.hasFlyInClaimPermission()) {
                return FlightAbility.NO_CLAIM;
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        }
    }

    /**
     * Owners, operators, members, and coop players belong to the island; only visitors do not.
     */
    public boolean isPlayerMemberOfIsland(Player player, Island island) {
        if (player.getUniqueId().equals(island.getOwnerUUID())) return true;

        IslandRole role = island.getRole(player);
        return role != null && role != IslandRole.VISITOR;
    }
}
