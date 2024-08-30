package host.plas.flyingallowed.compat.plugins.lands;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class LandsHeld extends HeldHolder {
    public LandsHeld() {
        super(CompatManager.LANDS_IDENTIFIER, new LandsHolderCreator());
    }
}
