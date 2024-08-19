package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.PlayerMoveData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

public class GriefPreventionHolder extends ApiHolder<GriefPrevention> {
    public GriefPreventionHolder() {
        super("lands", (v) -> GriefPrevention.instance);
    }

    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        Claim claim = api().dataStore.getClaimAt(moveData.getTo(), true, null);

        if (claim != null) {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.claims")) {
                if (claim.getOwnerID().equals(moveData.getPlayer().getUniqueId()) || claim.getPermission(moveData.getPlayer().getUniqueId().toString()) == ClaimPermission.Access) {
                    return FlightAbility.ABLE_TO_FLY;
                } else {
                    return FlightAbility.UNABLE_TO_FLY;
                }
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        } else {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.claims")) {
                return FlightAbility.NO_CLAIM;
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        }
    }
}
