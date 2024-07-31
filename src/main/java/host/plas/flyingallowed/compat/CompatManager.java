package host.plas.flyingallowed.compat;

import host.plas.flyingallowed.compat.integrated.GPHeld;
import host.plas.flyingallowed.compat.integrated.LandsHeld;
import host.plas.flyingallowed.utils.MessageUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentSkipListMap;

@Getter @Setter
public class CompatManager {
    @Getter @Setter
    private static ConcurrentSkipListMap<String, HeldHolder> holders = new ConcurrentSkipListMap<>();

    public static void init() {
        String landsIdentifier = "lands";
        HeldHolder landsHolder = new EmptyHolder(landsIdentifier);
        try {
            landsHolder = new LandsHeld();
        } catch (Throwable e) {
            MessageUtils.logInfo("Lands not found, skipping...");
        }
        holders.put(landsIdentifier, landsHolder);

        String griefPreventionIdentifier = "griefprevention";
        HeldHolder griefPreventionHolder = new EmptyHolder(griefPreventionIdentifier);
        try {
            griefPreventionHolder = new GPHeld();
        } catch (Throwable e) {
            MessageUtils.logInfo("GriefPrevention not found, skipping...");
        }
        holders.put(griefPreventionIdentifier, griefPreventionHolder);
    }

    public static HeldHolder getHolder(String identifier) {
        return holders.get(identifier);
    }

    public static boolean isEnabled(String identifier) {
        return getHolder(identifier) != null && getHolder(identifier).isEnabled();
    }

    public static HeldHolder getLandsHolder() {
        return getHolder("lands");
    }

    public static HeldHolder getGriefPreventionHolder() {
        return getHolder("griefprevention");
    }

    public static boolean isLandsEnabled() {
        return isEnabled("lands");
    }

    public static boolean isGriefPreventionEnabled() {
        return isEnabled("griefprevention");
    }
}
