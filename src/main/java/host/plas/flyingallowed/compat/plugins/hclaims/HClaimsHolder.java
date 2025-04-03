package host.plas.flyingallowed.compat.plugins.hclaims;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.user.OnlineUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HClaimsHolder extends FlyingHolder<HuskClaimsAPI> {
    public HClaimsHolder() {
        super(CompatManager.HCLAIMS_IDENTIFIER, (v) -> HuskClaimsAPI.getInstance(), FlightExtent.HUSK_CLAIMS);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        OnlineUser user = api().getOnlineUser(moveData.getPlayer().getUniqueId());

        Position position = Position.at(
                moveData.getTo().getX(),
                moveData.getTo().getY(),
                moveData.getTo().getZ(),
                api().getWorld(moveData.getToWorld().getName())
        );
        Claim claim = api().getClaimAt(position).orElse(null);

        if (claim != null) {
            if (moveData.getPlayer().hasPermission("flyingallowed.in.claims")) {
                if ((claim.getOwner().isPresent() && claim.getOwner().get().equals(moveData.getPlayer().getUniqueId())) ||
                        checkClaimExtents(user, position)) {
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

    public boolean checkClaimExtents(OnlineUser user, Position position) {
        AtomicBoolean fail = new AtomicBoolean(false);

        getConfigAllowedPermissions().forEach(extent -> {
            if (fail.get()) return;

            if (! api().isOperationAllowed(user, extent, position)) {
                fail.set(true);
            }
        });

        return ! fail.get();
    }

    public static boolean isConfigAllowedPermissionsAny() {
        return FlyingAllowed.getMainConfig().getHCClaimExtentsAllowed().stream().anyMatch(s -> s.equalsIgnoreCase("ANY"));
    }

    public static List<OperationType> getConfigAllowedPermissions() {
        List<OperationType> permissions = new ArrayList<>();

        if (isConfigAllowedPermissionsAny()) {
            permissions.addAll(OperationType.getRegistered());

            return permissions;
        }

        FlyingAllowed.getMainConfig().getHCClaimExtentsAllowed().forEach(extent -> {
            try {
                if (extent == null) return;
                OperationType.getRegistered().stream().filter(operationType -> operationType.getKey().asString().equals(extent)).findFirst().ifPresent(permissions::add);
            } catch (Throwable e) {
                try {
                    OperationType type = OperationType.valueOf(extent);
                    if (type == null) return;

                    permissions.add(type);
                } catch (Throwable t) {
                    FlyingAllowed.getInstance().getLogger().warning("Invalid permission in config: " + extent);
                }
            }
        });

        return permissions;
    }
}
