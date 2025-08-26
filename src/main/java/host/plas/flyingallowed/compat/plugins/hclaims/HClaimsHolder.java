package host.plas.flyingallowed.compat.plugins.hclaims;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.compat.plugins.sskyblock.SSkyblockHeld;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import net.william278.huskclaims.claim.Claim;
import net.william278.huskclaims.api.HuskClaimsAPI;
import net.william278.huskclaims.libraries.cloplib.operation.Operation;
import net.william278.huskclaims.libraries.cloplib.operation.OperationType;
import net.william278.huskclaims.position.Position;
import net.william278.huskclaims.user.OnlineUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class HClaimsHolder extends FlyingHolder<HuskClaimsAPI> {
    public HClaimsHolder() {
        super(HClaimsHeld.IDENTIFIER, (v) -> HuskClaimsAPI.getInstance(), FlightExtent.HUSK_CLAIMS);
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        OnlineUser user;
        try {
            user = api().getOnlineUser(moveData.getPlayer().getUniqueId());
        } catch (Throwable e) {
            if (e.getMessage().contains("Player is not online")) {
                FlyingAllowed.getInstance().logDebug("Checking for offline players is not allowed by HuskClaims.");
            }
            return FlightAbility.UNABLE_TO_FLY;
        }

        Position position = Position.at(
                moveData.getTo().getX(),
                moveData.getTo().getY(),
                moveData.getTo().getZ(),
                api().getWorld(moveData.getToWorld().getName())
        );
        Claim claim = api().getClaimAt(position).orElse(null);

        if (claim != null) {
            if (moveData.hasAllClaimsPermission()) {
                return FlightAbility.ABLE_TO_FLY;
            }

            if (moveData.hasFlyInClaimPermission()) {
                if (claim.getOwner().isPresent() && claim.getOwner().get().equals(moveData.getPlayer().getUniqueId())) {
                    return FlightAbility.ABLE_TO_FLY;
                } else if (isTrusted(claim, user, position)) {
                    return FlightAbility.ABLE_TO_FLY;
                } else if (checkClaimExtents(claim, user, position)) {
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

    public boolean isTrusted(Claim claim, OnlineUser user, Position position) {
        AtomicBoolean isTrusted = new AtomicBoolean(false);

        claim.getUserTrustLevel(user, api().getPlugin()).ifPresent(trustLevel -> {
            getConfigAllowedPermissions().forEach(operationType -> {
                if (isTrusted.get()) return;

                if (trustLevel.getFlags().contains(operationType)) {
                    if (isTrusted.get()) return;

                    isTrusted.set(true);
                }
            });
        });

        return isTrusted.get();
    }

    public boolean checkClaimExtents(Claim claim, OnlineUser user, Position position) {
        AtomicBoolean pass = new AtomicBoolean(false);

        if (claim.isUserBanned(user)) return false;

        getConfigAllowedPermissions().forEach(extent -> {
            if (pass.get()) return;

            if (claim.isOperationAllowed(Operation.of(user, extent, position), api().getPlugin())) {
                pass.set(true);
            }
        });

        return pass.get();
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
            if (extent == null) {
                FlyingAllowed.getInstance().logWarning("Invalid permission in config (null)...");
                return;
            }

            try {
                OperationType type = OperationType.getRegistered().stream().filter(operationType -> operationType.getKey().asString().equals(extent)).findFirst().orElseThrow();

                permissions.add(type);
            } catch (Throwable e) {
                try {
                    OperationType type = OperationType.valueOf(extent);
                    if (type == null) {
                        FlyingAllowed.getInstance().logWarning("Invalid permission in config (not found): " + extent);
                        return;
                    }

                    permissions.add(type);
                } catch (Throwable t) {
                    FlyingAllowed.getInstance().getLogger().warning("Invalid permission in config (error): " + extent);
                }
            }
        });

        return permissions;
    }
}
