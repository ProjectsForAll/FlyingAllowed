package host.plas.flyingallowed.compat.plugins.gp;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicBoolean;

public class GPHolder extends FlyingHolder<GriefPrevention> {
    public GPHolder() {
        super(CompatManager.GRIEF_PREVENTION_IDENTIFIER, (v) -> GriefPrevention.instance, FlightExtent.GRIEF_PREVENTION);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        Claim claim = api().dataStore.getClaimAt(moveData.getTo(), true, null);

        if (claim != null) {
            if (moveData.hasFlyInClaimPermission()) {
                if (claim.getOwnerID().equals(moveData.getPlayer().getUniqueId()) || checkClaimExtents(moveData.getPlayer(), claim)) {
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

    public static boolean checkClaimExtents(Player player, Claim claim) {
        AtomicBoolean fail = new AtomicBoolean(false);

        getConfigAllowedPermissions().forEach(extent -> {
            if (fail.get()) return;

            ClaimPermission permission = claim.getPermission(player.getUniqueId().toString());
            if (permission == null) {
                fail.set(true);
            } else if (permission != extent) {
                fail.set(true);
            }
        });

        return ! fail.get();
    }

    public static boolean isConfigAllowedPermissionsAny() {
        return FlyingAllowed.getMainConfig().getGPClaimExtentsAllowed().stream().anyMatch(s -> s.equalsIgnoreCase("ANY"));
    }

    public static ConcurrentSkipListSet<ClaimPermission> getConfigAllowedPermissions() {
        ConcurrentSkipListSet<ClaimPermission> permissions = new ConcurrentSkipListSet<>();

        if (isConfigAllowedPermissionsAny()) {
            permissions.addAll(Arrays.asList(ClaimPermission.values()));

            return permissions;
        }

        FlyingAllowed.getMainConfig().getGPClaimExtentsAllowed().forEach(extent -> {
            try {
                permissions.add(ClaimPermission.valueOf(extent));
            } catch (IllegalArgumentException e) {
                FlyingAllowed.getInstance().getLogger().warning("Invalid permission in config: " + extent);
            }
        });

        return permissions;
    }
}
