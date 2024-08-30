package host.plas.flyingallowed.compat.plugins.pstones;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class PStonesHeld extends HeldHolder {
    public PStonesHeld() {
        super(CompatManager.PS_IDENTIFIER, new PStonesHolderCreator());
    }
}
