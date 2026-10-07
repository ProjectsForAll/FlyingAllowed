package host.plas.flyingallowed.compat.plugins.lands;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.land.LandWorld;

public class LandsHolder extends FlyingHolder<LandsIntegration> {
    public LandsHolder() {
        super(LandsHeld.IDENTIFIER, (v) -> LandsIntegration.of(FlyingAllowed.getInstance()), FlightExtent.LANDS);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        LandWorld toLandWorld = api().getWorld(moveData.getToWorld());
        if (toLandWorld == null) return FlightAbility.ISSUE;

        // Chunk coordinates from block coordinates, so the lookup never loads the chunk.
        Land land = toLandWorld.getLandByChunk(moveData.getTo().getBlockX() >> 4, moveData.getTo().getBlockZ() >> 4);

        if (land != null) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.hasFlyInClaimPermission()) {
                if (moveData.getPlayer().getUniqueId().equals(land.getOwnerUID()) || land.isTrusted(moveData.getPlayer().getUniqueId())) {
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
}
