package host.plas.flyingallowed.compat.plugins.wg;

import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;

public class WGHolder extends FlyingHolder<WorldGuardPlugin> {
    public static final String FLIGHT_FLAG_NAME = "flyingallowed-allow-flight";
    @Getter @Setter
    private static StateFlag flightFlag;

    public WGHolder() {
        super(CompatManager.WG_IDENTIFIER, (v) -> WorldGuardPlugin.inst(), FlightExtent.WORLDGUARD);
    }

    public static void registerFlightFlag() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag(FLIGHT_FLAG_NAME, false);
            registry.register(flag);

            setFlightFlag(flag);
        } catch (FlagConflictException e) {
            FlyingAllowed.getInstance().logWarning("WorldGuard flight flag already registered, using existing flag.");
            Flag<?> existingFlag = registry.get(FLIGHT_FLAG_NAME);
            if (existingFlag instanceof StateFlag) {
                setFlightFlag((StateFlag) existingFlag);
            } else {
                FlyingAllowed.getInstance().logWarning("Existing flag is not a StateFlag, flight flag will not work.");
            }
        }
    }

    @Override
    public FlightAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return FlightAbility.NO_API;

        BlockVector3 position = BlockVector3.at(
                moveData.getTo().getX(),
                moveData.getTo().getY(),
                moveData.getTo().getZ()
        );
        RegionManager regionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(moveData.getToWorld()));
        if (regionManager == null) return FlightAbility.ISSUE;
        ApplicableRegionSet regionSet = regionManager.getApplicableRegions(position);

        LocalPlayer player;
        try {
            player = api().wrapPlayer(moveData.getPlayer());
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logWarning("Failed to get LocalPlayer from Player... This is not supposed to happen.");
            FlyingAllowed.getInstance().logWarning(e);
            return FlightAbility.NO_API;
        }

        if (regionSet.size() > 0) {
            if (moveData.hasFlyInRegionPermission()) {
                if (checkClaimExtents(player, regionSet)) {
                    return FlightAbility.ABLE_TO_FLY;
                } else {
                    return FlightAbility.UNABLE_TO_FLY;
                }
            }
            if (moveData.hasFlyInClaimPermission()) {
                if (regionSet.isOwnerOfAll(player) || regionSet.isMemberOfAll(player)) {
                    return FlightAbility.ABLE_TO_FLY;
                } else {
                    return FlightAbility.UNABLE_TO_FLY;
                }
            }
            return FlightAbility.UNABLE_TO_FLY;
        } else {
            if (moveData.hasFlyInClaimPermission()) {
                return FlightAbility.NO_CLAIM;
            } else {
                return FlightAbility.UNABLE_TO_FLY;
            }
        }
    }

    public boolean checkClaimExtents(LocalPlayer player, ApplicableRegionSet regionSet) {
        AtomicBoolean fail = new AtomicBoolean(false);
        AtomicBoolean allow = new AtomicBoolean(false);

        regionSet.forEach(region -> {
            if (fail.get()) return;

            StateFlag.State state = region.getFlag(getFlightFlag());
            if (state != null) {
                if (state == StateFlag.State.ALLOW) {
                    allow.set(true);
                }
                if (state == StateFlag.State.DENY) {
                    fail.set(true);
                }
            }
        });

        return allow.get() && ! fail.get();
    }
}
