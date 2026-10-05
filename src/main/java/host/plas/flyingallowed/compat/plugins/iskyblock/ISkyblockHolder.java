package host.plas.flyingallowed.compat.plugins.iskyblock;

import com.iridium.iridiumskyblock.IridiumSkyblock;
import com.iridium.iridiumskyblock.api.IridiumSkyblockAPI;
import com.iridium.iridiumskyblock.database.Island;
import com.iridium.iridiumskyblock.database.User;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;

public class ISkyblockHolder extends FlyingHolder<IridiumSkyblockAPI> {
    public ISkyblockHolder() {
        super(ISkyblockHeld.IDENTIFIER, (v) -> IridiumSkyblockAPI.getInstance(), FlightExtent.IRIDIUM_SKYBLOCK);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        if (! api().isIslandWorld(moveData.getToWorld())) {
            return FlightAbility.NO_CLAIM;
        }

        Island island = api().getIslandViaLocation(moveData.getTo()).orElse(null);

        if (island != null) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.hasFlyInClaimPermission()) {
                User user = api().getUser(moveData.getPlayer());
                if (isMember(user, island) || isTrusted(user, island)) {
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

    public boolean isMember(User user, Island island) {
        return user.getIsland().map(own -> own.getId() == island.getId()).orElse(false);
    }

    public boolean isTrusted(User user, Island island) {
        return IridiumSkyblock.getInstance().getIslandManager().getTeamTrust(island, user).isPresent();
    }
}
