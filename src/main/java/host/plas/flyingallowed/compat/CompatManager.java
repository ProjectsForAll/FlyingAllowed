package host.plas.flyingallowed.compat;

import host.plas.bou.compat.EmptyHolder;
import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.plugins.gp.GPHeld;
import host.plas.flyingallowed.compat.plugins.gp.GriefPreventionHolder;
import host.plas.flyingallowed.compat.plugins.kingdoms.KingdomsHeld;
import host.plas.flyingallowed.compat.plugins.kingdoms.KingdomsHolder;
import host.plas.flyingallowed.compat.plugins.lands.LandsHeld;
import host.plas.flyingallowed.compat.plugins.lands.LandsHolder;
import host.plas.flyingallowed.compat.plugins.pstones.PStonesHeld;
import host.plas.flyingallowed.compat.plugins.pstones.PStonesHolder;
import host.plas.flyingallowed.compat.plugins.sskyblock.SSkyblockHeld;
import host.plas.flyingallowed.compat.plugins.sskyblock.SSkyblockHolder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompatManager {
    public static final String LANDS_IDENTIFIER = "Lands";
    public static final String GRIEF_PREVENTION_IDENTIFIER = "GriefPrevention";
    public static final String KINGDOMS_IDENTIFIER = "KingdomsX";
    public static final String SS_IDENTIFIER = "SuperiorSkyblock2";
    public static final String PS_IDENTIFIER = "ProtectionStones";

    public static void init() {
        HeldHolder landsHolder = new EmptyHolder(LANDS_IDENTIFIER);
        try {
            landsHolder = new LandsHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("Lands not found, skipping...");
        }
        putHolder(LANDS_IDENTIFIER, landsHolder);

        HeldHolder griefPreventionHolder = new EmptyHolder(GRIEF_PREVENTION_IDENTIFIER);
        try {
            griefPreventionHolder = new GPHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("GriefPrevention not found, skipping...");
        }
        putHolder(GRIEF_PREVENTION_IDENTIFIER, griefPreventionHolder);

        HeldHolder kingdomsHolder = new EmptyHolder(KINGDOMS_IDENTIFIER);
        try {
            kingdomsHolder = new KingdomsHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("KingdomsX not found, skipping...");
        }
        putHolder(KINGDOMS_IDENTIFIER, kingdomsHolder);

        HeldHolder ssHolder = new EmptyHolder(SS_IDENTIFIER);
        try {
            ssHolder = new SSkyblockHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("SuperiorSkyblock2 not found, skipping...");
        }
        putHolder(SS_IDENTIFIER, ssHolder);

        HeldHolder psHolder = new EmptyHolder(PS_IDENTIFIER);
        try {
            psHolder = new PStonesHeld();
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo("ProtectionStones not found, skipping...");
        }
        putHolder(PS_IDENTIFIER, psHolder);
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

    public static GriefPreventionHolder getGriefPreventionHolder() {
        return (GriefPreventionHolder) getHolder(GRIEF_PREVENTION_IDENTIFIER).getHolder();
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
}