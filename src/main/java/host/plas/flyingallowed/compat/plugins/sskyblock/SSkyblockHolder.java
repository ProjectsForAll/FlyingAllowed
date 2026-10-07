package host.plas.flyingallowed.compat.plugins.sskyblock;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblock;
import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.wrappers.SuperiorPlayer;
import com.bgsoftware.superiorskyblock.api.island.Island;
import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.entity.Player;

public class SSkyblockHolder extends FlyingHolder<SuperiorSkyblock> {
    public SSkyblockHolder() {
        super(SSkyblockHeld.IDENTIFIER, (v) -> SuperiorSkyblockAPI.getSuperiorSkyblock(), FlightExtent.SUPERIOR_SKYBLOCK);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        if (! api().getGrid().isIslandsWorld(moveData.getTo().getWorld())) {
            return FlightAbility.NO_CLAIM;
        }

        Island island = api().getGrid().getIslandAt(moveData.getTo());

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
     * Island members (owner included) and coop players belong to the island; visitors do not.
     */
    public boolean isPlayerMemberOfIsland(Player player, Island island) {
        SuperiorPlayer superiorPlayer = SuperiorSkyblockAPI.getPlayer(player);
        if (superiorPlayer == null) return false;

        return island.isMember(superiorPlayer) || island.isCoop(superiorPlayer);
    }
}
