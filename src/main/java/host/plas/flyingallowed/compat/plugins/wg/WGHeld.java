package host.plas.flyingallowed.compat.plugins.wg;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class WGHeld extends HeldHolder {
    public WGHeld() {
        super(CompatManager.WG_IDENTIFIER, new WGHolderCreator());
    }
}
