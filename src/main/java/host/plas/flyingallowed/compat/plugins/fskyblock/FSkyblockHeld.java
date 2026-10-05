package host.plas.flyingallowed.compat.plugins.fskyblock;

import host.plas.bou.compat.HeldHolder;

public class FSkyblockHeld extends HeldHolder {
    public static String IDENTIFIER = "FabledSkyBlock";

    public FSkyblockHeld() {
        super(IDENTIFIER, new FSkyblockHolderCreator());
    }
}
