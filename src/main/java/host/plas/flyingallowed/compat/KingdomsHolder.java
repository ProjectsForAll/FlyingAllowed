package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.entity.Player;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.land.Land;
import org.kingdoms.constants.land.location.SimpleChunkLocation;
import org.kingdoms.main.Kingdoms;

import java.util.concurrent.atomic.AtomicBoolean;

public class KingdomsHolder extends ApiHolder<Kingdoms> {
    public KingdomsHolder() {
        super("kingdoms", (v) -> Kingdoms.get());
    }

    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        SimpleChunkLocation chunkLocation = SimpleChunkLocation.of(moveData.getTo());
        Land land = Land.getLand(chunkLocation);

        if (land != null) {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.lands")) {
                Kingdom kingdom = land.getKingdom();
                if (kingdom == null) {
                    return FlightAbility.NO_CLAIM;
                }
                AtomicBoolean canBuild = new AtomicBoolean(false);
                if (kingdom.getOwnerId().equals(moveData.getPlayer().getUniqueId())) {
                    canBuild.set(true);
                }
                if (canBuild.get()) {
                    return FlightAbility.ABLE_TO_FLY;
                }
                kingdom.getPlayerMembers().forEach(offline -> {
                    if (canBuild.get()) return;

                    if (! (offline instanceof Player)) return;
                    if (offline.getPlayer() == null) return;
                    Player player = offline.getPlayer();
                    if (player == null) return;

                    if (player.getUniqueId().equals(moveData.getPlayer().getUniqueId())) {
                        canBuild.set(true);
                    }
                });

                if (canBuild.get()) {
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
