package host.plas.flyingallowed.compat;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.handlers.RolesManager;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.PlayerRole;
import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.entity.Player;

public class SSkyblockHolder extends ApiHolder<SuperiorSkyblock> {
    public SSkyblockHolder() {
        super("lands", (v) -> SuperiorSkyblockAPI.getSuperiorSkyblock());
    }

    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        if (! api().getGrid().isIslandsWorld(moveData.getTo().getWorld())) {
            return FlightAbility.NO_CLAIM;
        }

        Island island = api().getGrid().getIslandAt(moveData.getTo());

        if (island != null) {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.lands")) {
                if (isPlayerMemberOfIsland(moveData.getPlayer(), island)) {
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

    public boolean isPlayerMemberOfIsland(Player player, Island island) {
        if (island.getOwner().getUniqueId().equals(player.getUniqueId())) return true;

        RolesManager rolesManager = api().getRoles();

        boolean isCoop = isPlayerMember(player, island, rolesManager.getCoopRole());
        boolean isGuest = isPlayerMember(player, island, rolesManager.getGuestRole());

        return isCoop || ! isGuest;
    }

    public boolean isPlayerMember(Player player, Island island, PlayerRole role) {
        return island.getIslandMembers(role).stream().anyMatch(member -> member.getUniqueId().equals(player.getUniqueId()));
    }
}
