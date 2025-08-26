package host.plas.flyingallowed.compat.plugins.kingdoms;

import host.plas.bou.compat.HeldHolder;

public class KingdomsHeld extends HeldHolder {
    public static String IDENTIFIER = "KingdomsX";

    public KingdomsHeld() {
        super(IDENTIFIER, new KingdomsHolderCreator());
    }
}
