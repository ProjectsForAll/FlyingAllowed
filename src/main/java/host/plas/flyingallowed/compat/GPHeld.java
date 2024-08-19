package host.plas.flyingallowed.compat;

import host.plas.bou.compat.HeldHolder;

public class GPHeld extends HeldHolder {
    public GPHeld() {
        super("Lands", new GPHolderCreator());
    }
}
