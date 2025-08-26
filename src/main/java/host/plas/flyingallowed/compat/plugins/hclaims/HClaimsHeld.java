package host.plas.flyingallowed.compat.plugins.hclaims;

import host.plas.bou.compat.HeldHolder;

public class HClaimsHeld extends HeldHolder {
    public static String IDENTIFIER = "HuskClaims";

    public HClaimsHeld() {
        super(IDENTIFIER, new HClaimsHolderCreator());
    }
}
