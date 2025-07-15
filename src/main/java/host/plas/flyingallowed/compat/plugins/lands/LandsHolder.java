package host.plas.flyingallowed.compat.plugins.lands;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.angeschossen.lands.api.land.LandWorld;
import org.bukkit.Chunk;

public class LandsHolder extends FlyingHolder<LandsIntegration> {
    public LandsHolder() {
        super(CompatManager.LANDS_IDENTIFIER, (v) -> LandsIntegration.of(FlyingAllowed.getInstance()), FlightExtent.LANDS);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        LandWorld toLandWorld = api().getWorld(moveData.getToWorld());
        if (toLandWorld == null) return FlightAbility.ISSUE;

        Chunk chunk = moveData.getTo().getChunk();

        Land land = toLandWorld.getLandByChunk(chunk.getX(), chunk.getZ());

        if (land != null) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.getPlayer().hasPermission("flyingallowed.in.lands")) {
                if (land.getOwnerUID().equals(moveData.getPlayer().getUniqueId()) || land.isTrusted(moveData.getPlayer().getUniqueId())) {
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
}
