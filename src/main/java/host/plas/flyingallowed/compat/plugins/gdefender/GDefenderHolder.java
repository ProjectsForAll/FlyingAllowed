package host.plas.flyingallowed.compat.plugins.gdefender;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import org.bukkit.entity.Player;

public class GDefenderHolder extends FlyingHolder<Core> {
    public GDefenderHolder() {
        super(GDefenderHeld.IDENTIFIER, (v) -> GriefDefender.getCore(), FlightExtent.GRIEF_PREVENTION);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        Claim claim = api().getClaimAt(moveData.getTo());

        if (claim != null && !claim.isWilderness()) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.hasFlyInClaimPermission()) {
                if (claim.getOwnerUniqueId().equals(moveData.getPlayer().getUniqueId()) || checkClaimExtents(moveData.getPlayer(), claim)) {
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

    public boolean checkClaimExtents(Player player, Claim claim) {
        if (claim.getOwnerUniqueId().equals(player.getUniqueId())) return true;

        // Don't need to check subdivisions, as we want per claim / subdivision lookup.
//        if (claim.isSubdivision()) {
//            Claim parent = claim.getParent();
//            if (parent != null) {
//                return checkClaimExtents(player, parent);
//            }
//            return false;
//        }

        // check if is trusted
        return claim.getUserTrusts().stream().anyMatch(u -> u.equals(player.getUniqueId()));
    }

//    public static boolean checkClaimExtentsHard(Player player, Claim claim) {
//        AtomicBoolean pass = new AtomicBoolean(false);
//
//        getConfigAllowedPermissions().forEach(extent -> {
//            if (pass.get()) return;
//
//             permission = claim.getPermission(player.getUniqueId().toString());
//            if (permission != null) {
//                if (permission == extent) {
//                    pass.set(true);
//                }
//            }
//        });
//
//        return pass.get();
//    }
//
//    public static boolean isConfigAllowedPermissionsAny() {
//        return FlyingAllowed.getMainConfig().getGPClaimExtentsAllowed().stream().anyMatch(s -> s.equalsIgnoreCase("ANY"));
//    }
//
//    public static ConcurrentSkipListSet<ClaimPermission> getConfigAllowedPermissions() {
//        ConcurrentSkipListSet<ClaimPermission> permissions = new ConcurrentSkipListSet<>();
//
//        if (isConfigAllowedPermissionsAny()) {
//            permissions.addAll(Arrays.asList(ClaimPermission.values()));
//
//            return permissions;
//        }
//
//        FlyingAllowed.getMainConfig().getGPClaimExtentsAllowed().forEach(extent -> {
//            try {
//                permissions.add(ClaimPermission.valueOf(extent));
//            } catch (IllegalArgumentException e) {
//                FlyingAllowed.getInstance().getLogger().warning("Invalid permission in config: " + extent);
//            }
//        });
//
//        return permissions;
//    }
}
