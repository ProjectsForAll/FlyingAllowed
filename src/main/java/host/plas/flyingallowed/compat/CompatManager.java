package host.plas.flyingallowed.compat;

import host.plas.bou.compat.EmptyHolder;
import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.compat.plugins.gp.GPHeld;
import host.plas.flyingallowed.compat.plugins.gp.GPHolder;
import host.plas.flyingallowed.compat.plugins.hclaims.HClaimsHeld;
import host.plas.flyingallowed.compat.plugins.hclaims.HClaimsHolder;
import host.plas.flyingallowed.compat.plugins.kingdoms.KingdomsHeld;
import host.plas.flyingallowed.compat.plugins.kingdoms.KingdomsHolder;
import host.plas.flyingallowed.compat.plugins.lands.LandsHeld;
import host.plas.flyingallowed.compat.plugins.lands.LandsHolder;
import host.plas.flyingallowed.compat.plugins.pstones.PStonesHeld;
import host.plas.flyingallowed.compat.plugins.pstones.PStonesHolder;
import host.plas.flyingallowed.compat.plugins.sskyblock.SSkyblockHeld;
import host.plas.flyingallowed.compat.plugins.sskyblock.SSkyblockHolder;
import host.plas.flyingallowed.compat.plugins.wg.WGHeld;
import host.plas.flyingallowed.compat.plugins.wg.WGHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import lombok.Getter;
import lombok.Setter;
import tv.quaint.objects.SingleSet;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class CompatManager {
    public static final String LANDS_IDENTIFIER = "Lands";
    public static final String GRIEF_PREVENTION_IDENTIFIER = "GriefPrevention";
    public static final String KINGDOMS_IDENTIFIER = "KingdomsX";
    public static final String SS_IDENTIFIER = "SuperiorSkyblock2";
    public static final String PS_IDENTIFIER = "ProtectionStones";
    public static final String HCLAIMS_IDENTIFIER = "HuskClaims";
    public static final String WG_IDENTIFIER = "WorldGuard";

    public static void init() {
        HeldHolder landsHolder = new EmptyHolder(LANDS_IDENTIFIER);
        try {
            landsHolder = new LandsHeld();
            FlyingAllowed.getInstance().logInfo("Lands found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("Lands not found, skipping...");
        }
        putHolder(LANDS_IDENTIFIER, landsHolder);

        HeldHolder griefPreventionHolder = new EmptyHolder(GRIEF_PREVENTION_IDENTIFIER);
        try {
            griefPreventionHolder = new GPHeld();
            FlyingAllowed.getInstance().logInfo("GriefPrevention found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("GriefPrevention not found, skipping...");
        }
        putHolder(GRIEF_PREVENTION_IDENTIFIER, griefPreventionHolder);

        HeldHolder kingdomsHolder = new EmptyHolder(KINGDOMS_IDENTIFIER);
        try {
            kingdomsHolder = new KingdomsHeld();
            FlyingAllowed.getInstance().logInfo("KingdomsX found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("KingdomsX not found, skipping...");
        }
        putHolder(KINGDOMS_IDENTIFIER, kingdomsHolder);

        HeldHolder ssHolder = new EmptyHolder(SS_IDENTIFIER);
        try {
            ssHolder = new SSkyblockHeld();
            FlyingAllowed.getInstance().logInfo("SuperiorSkyblock2 found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("SuperiorSkyblock2 not found, skipping...");
        }
        putHolder(SS_IDENTIFIER, ssHolder);

        HeldHolder psHolder = new EmptyHolder(PS_IDENTIFIER);
        try {
            psHolder = new PStonesHeld();
            FlyingAllowed.getInstance().logInfo("ProtectionStones found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("ProtectionStones not found, skipping...");
        }
        putHolder(PS_IDENTIFIER, psHolder);

        HeldHolder hcHolder = new EmptyHolder(HCLAIMS_IDENTIFIER);
        try {
            hcHolder = new HClaimsHeld();
            FlyingAllowed.getInstance().logInfo("HuskClaims found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("HuskClaims not found, skipping...");
        }
        putHolder(HCLAIMS_IDENTIFIER, hcHolder);

        HeldHolder wgHolder = new EmptyHolder(WG_IDENTIFIER);
        try {
            wgHolder = new WGHeld();
            FlyingAllowed.getInstance().logInfo("HuskClaims found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("HuskClaims not found, skipping...");
        }
        putHolder(WG_IDENTIFIER, wgHolder);
    }

    public static void putHolder(String identifier, HeldHolder holder) {
        host.plas.bou.compat.CompatManager.getHolders().put(identifier, holder);
    }

    public static HeldHolder getHolder(String identifier) {
        return host.plas.bou.compat.CompatManager.getHolders().get(identifier);
    }

    public static boolean isEnabled(String identifier) {
        return getHolder(identifier) != null && getHolder(identifier).isEnabled();
    }

    public static LandsHolder getLandsHolder() {
        return (LandsHolder) getHolder(LANDS_IDENTIFIER).getHolder();
    }

    public static GPHolder getGriefPreventionHolder() {
        return (GPHolder) getHolder(GRIEF_PREVENTION_IDENTIFIER).getHolder();
    }

    public static KingdomsHolder getKingdomsHolder() {
        return (KingdomsHolder) getHolder(KINGDOMS_IDENTIFIER).getHolder();
    }

    public static SSkyblockHolder getSSHolder() {
        return (SSkyblockHolder) getHolder(SS_IDENTIFIER).getHolder();
    }

    public static PStonesHolder getPStonesHolder() {
        return (PStonesHolder) getHolder(PS_IDENTIFIER).getHolder();
    }

    public static HClaimsHolder getHClaimsHolder() {
        return (HClaimsHolder) getHolder(HCLAIMS_IDENTIFIER).getHolder();
    }

    public static WGHolder getWGHolder() {
        return (WGHolder) getHolder(WG_IDENTIFIER).getHolder();
    }

    public static boolean isLandsEnabled() {
        return isEnabled(LANDS_IDENTIFIER);
    }

    public static boolean isGriefPreventionEnabled() {
        return isEnabled(GRIEF_PREVENTION_IDENTIFIER);
    }

    public static boolean isKingdomsEnabled() {
        return isEnabled(KINGDOMS_IDENTIFIER);
    }

    public static boolean isSSEnabled() {
        return isEnabled(SS_IDENTIFIER);
    }

    public static boolean isPStonesEnabled() {
        return isEnabled(PS_IDENTIFIER);
    }

    public static boolean isHClaimsEnabled() {
        return isEnabled(HCLAIMS_IDENTIFIER);
    }

    public static boolean isWGEnabled() {
        return isEnabled(WG_IDENTIFIER);
    }

    public static SingleSet<FlightAbility, FlightExtent> getFlyingAllowed(PlayerMoveData data) {
        List<SingleSet<FlightAbility, FlightExtent>> list = new ArrayList<>();

        for (HeldHolder holder : host.plas.bou.compat.CompatManager.getHolders().values()) {
            if (! (holder.getHolder() instanceof FlyingHolder)) continue;
            FlyingHolder<?> flyingHolder = (FlyingHolder<?>) holder.getHolder();

            if (holder.isEnabled()) {
                SingleSet<FlightAbility, FlightExtent> set = flyingHolder.wrapFlyable(data);
                if (set != null) {
                    list.add(set);
                }
            }
        }

        SingleSet<FlightAbility, FlightExtent> result = new SingleSet<>(FlightAbility.NONE, FlightExtent.NONE);
        for (SingleSet<FlightAbility, FlightExtent> set : list) {
            result = checkAndSet(set, result);
        }

        return result;
    }

    public static SingleSet<FlightAbility, FlightExtent> checkAndSet
            (SingleSet<FlightAbility, FlightExtent> set, SingleSet<FlightAbility, FlightExtent> result) {
        FlightAbility ability = set.getKey();
        FlightExtent extent = set.getValue();

        if (result.getKey() == FlightAbility.ABLE_TO_FLY) return result;
        if (ability == FlightAbility.ABLE_TO_FLY) {
            result = set;
        }

        if (result.getKey() == FlightAbility.UNABLE_TO_FLY) return result;
        if (ability == FlightAbility.UNABLE_TO_FLY) {
            result = set;
        }

        if (result.getKey() == FlightAbility.NO_CLAIM) return result;
        if (ability == FlightAbility.NO_CLAIM) {
            result = set;
        }

        return result;
    }
}