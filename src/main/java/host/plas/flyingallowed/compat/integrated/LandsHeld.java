package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.compat.HeldHolder;

public class LandsHeld extends HeldHolder {
    public LandsHeld() {
        super("Lands", new LandsHolderCreator());
    }
}
