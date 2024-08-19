package host.plas.flyingallowed.compat;

import host.plas.bou.compat.HeldHolder;

public class LandsHeld extends HeldHolder {
    public LandsHeld() {
        super("Lands", new LandsHolderCreator());
    }
}
