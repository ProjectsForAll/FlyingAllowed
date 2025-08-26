package host.plas.flyingallowed.compat.plugins.lands;

import host.plas.bou.compat.HeldHolder;

public class LandsHeld extends HeldHolder {
    public static String IDENTIFIER = "Lands";

    public LandsHeld() {
        super(IDENTIFIER, new LandsHolderCreator());
    }
}
