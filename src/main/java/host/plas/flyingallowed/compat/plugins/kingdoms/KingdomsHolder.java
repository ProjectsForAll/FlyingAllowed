package host.plas.flyingallowed.compat.plugins.kingdoms;

import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.main.Kingdoms;

import java.util.UUID;

public class KingdomsHolder extends FlyingHolder<Kingdoms> {
    public KingdomsHolder() {
        super(KingdomsHeld.IDENTIFIER, (v) -> Kingdoms.get(), FlightExtent.KINGDOMSX);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        SimpleChunkLocation chunkLocation = SimpleChunkLocation.of(moveData.getTo());
        Land land = Land.getLand(chunkLocation);

        // A Land record can outlive its claim; without a kingdom the chunk is unclaimed.
        Kingdom kingdom = land == null ? null : land.getKingdom();

        if (kingdom != null) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.hasFlyInClaimPermission()) {
                UUID uuid = moveData.getPlayer().getUniqueId();
                if (uuid.equals(kingdom.getOwnerId()) || kingdom.isMember(uuid)) {
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
