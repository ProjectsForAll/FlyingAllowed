package host.plas.flyingallowed.compat.plugins.ifactions;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import io.github.toberocat.improvedfactions.ImprovedFactionsPlugin;
import io.github.toberocat.improvedfactions.claims.FactionClaim;
import io.github.toberocat.improvedfactions.claims.FactionClaimHandlerKt;
import io.github.toberocat.improvedfactions.user.FactionUser;
import io.github.toberocat.improvedfactions.user.FactionUserHandlerKt;
import io.github.toberocat.improvedfactions.zone.ZoneHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt;

public class IFactionsHolder extends FlyingHolder<ImprovedFactionsPlugin> {
    /**
     * Zones that Improved Factions treats as wilderness when no faction owns the chunk.
     * Any other faction-less zone (safezone, warzone, custom zones) is admin land.
     */
    public static final String UNMANAGED_ZONE_TYPE = "unmanaged";

    public IFactionsHolder() {
        super(IFactionsHeld.IDENTIFIER, (v) -> {
            Plugin plugin = Bukkit.getPluginManager().getPlugin(IFactionsHeld.IDENTIFIER);
            if (! (plugin instanceof ImprovedFactionsPlugin) || ! plugin.isEnabled()) return null;
            return (ImprovedFactionsPlugin) plugin;
        }, FlightExtent.IMPROVED_FACTIONS);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        try {
            // Improved Factions entities are Exposed DAOs, so every read must happen inside a
            // transaction; the verdict is computed in here so no entity escapes it.
            return ThreadLocalTransactionManagerKt.transaction(null, tx -> resolve(moveData));
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logWarning("Failed to query Improved Factions claim: " + e.getMessage());
            return FlightAbility.ISSUE;
        }
    }

    private FlightAbility resolve(PlayerMoveData moveData) {
        Location to = moveData.getTo();
        // Chunk coordinates from block coordinates, so the lookup never loads the chunk.
        FactionClaim claim = FactionClaimHandlerKt.getFactionClaim(
                to.getBlockX() >> 4,
                to.getBlockZ() >> 4,
                moveData.getToWorld().getName()
        );

        if (claim == null || isWilderness(claim)) {
            if (moveData.hasFlyInClaimPermission()) {
                return FlightAbility.NO_CLAIM;
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        }

        if (moveData.hasAllClaimsPermission()) {
            return FlightAbility.ABLE_TO_FLY;
        }

        if (! moveData.hasFlyInClaimPermission()) {
            return FlightAbility.UNABLE_TO_FLY;
        }

        // Faction-less zones (safezone, warzone, ...) belong to no player.
        if (! claim.isClaimed()) {
            return FlightAbility.UNABLE_TO_FLY;
        }

        FactionUser user = FactionUserHandlerKt.factionUser(moveData.getPlayer().getUniqueId());
        if (user.isInFaction() && user.getFactionId() == claim.getFactionId()) {
            return FlightAbility.ABLE_TO_FLY;
        } else {
            return FlightAbility.UNABLE_TO_FLY;
        }
    }

    public static boolean isWilderness(FactionClaim claim) {
        if (claim.isClaimed()) return false;

        String zone = claim.getZoneType();
        return zone == null
                || zone.equals(ZoneHandler.FACTION_ZONE_TYPE)
                || zone.equals(UNMANAGED_ZONE_TYPE);
    }
}
