package host.plas.flyingallowed.compat.plugins.hclaims;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class HClaimsHeld extends HeldHolder {
    public HClaimsHeld() {
        super(CompatManager.HCLAIMS_IDENTIFIER, new HClaimsHolderCreator());
    }
}
