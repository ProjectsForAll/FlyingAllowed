package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.compat.HeldHolder;

public class GPHeld extends HeldHolder {
    public GPHeld() {
        super("Lands", new GPHolderCreator());
    }
}
