package host.plas.flyingallowed.compat.plugins.iskyblock;

import host.plas.bou.compat.HeldHolder;

public class ISkyblockHeld extends HeldHolder {
    public static String IDENTIFIER = "IridiumSkyblock";

    public ISkyblockHeld() {
        super(IDENTIFIER, new ISkyblockHolderCreator());
    }
}
