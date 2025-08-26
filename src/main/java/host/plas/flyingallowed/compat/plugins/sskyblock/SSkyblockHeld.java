package host.plas.flyingallowed.compat.plugins.sskyblock;

import host.plas.bou.compat.HeldHolder;

public class SSkyblockHeld extends HeldHolder {
    public static String IDENTIFIER = "SuperiorSkyblock2";

    public SSkyblockHeld() {
        super(IDENTIFIER, new SSkyblockHolderCreator());
    }
}
