package host.plas.flyingallowed.compat;

import host.plas.bou.compat.HeldHolder;

public class KingdomsHeld extends HeldHolder {
    public KingdomsHeld() {
        super("Lands", new KingdomsHolderCreator());
    }
}
