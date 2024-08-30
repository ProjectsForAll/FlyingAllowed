package host.plas.flyingallowed.compat.plugins.gp;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class GPHeld extends HeldHolder {
    public GPHeld() {
        super(CompatManager.GRIEF_PREVENTION_IDENTIFIER, new GPHolderCreator());
    }
}
