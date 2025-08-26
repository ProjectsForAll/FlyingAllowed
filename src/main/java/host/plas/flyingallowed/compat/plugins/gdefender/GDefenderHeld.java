package host.plas.flyingallowed.compat.plugins.gdefender;

import host.plas.bou.compat.HeldHolder;

public class GDefenderHeld extends HeldHolder {
    public static String IDENTIFIER = "GriefDefender";

    public GDefenderHeld() {
        super(IDENTIFIER, new GDefenderHolderCreator());
    }
}
