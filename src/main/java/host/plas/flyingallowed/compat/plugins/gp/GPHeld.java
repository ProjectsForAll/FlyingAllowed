package host.plas.flyingallowed.compat.plugins.gp;

import host.plas.bou.compat.HeldHolder;

public class GPHeld extends HeldHolder {
    public static String IDENTIFIER = "GriefPrevention";

    public GPHeld() {
        super(IDENTIFIER, new GPHolderCreator());
    }
}
