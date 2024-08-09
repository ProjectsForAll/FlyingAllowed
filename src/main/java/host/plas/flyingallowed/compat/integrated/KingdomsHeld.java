package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.compat.HeldHolder;

public class KingdomsHeld extends HeldHolder {
    public KingdomsHeld() {
        super("Lands", new KingdomsHolderCreator());
    }
}
