package host.plas.flyingallowed.compat.plugins.pstones;

import host.plas.bou.compat.HeldHolder;

public class PStonesHeld extends HeldHolder {
    public static String IDENTIFIER = "ProtectionStones";

    public PStonesHeld() {
        super(IDENTIFIER, new PStonesHolderCreator());
    }
}
