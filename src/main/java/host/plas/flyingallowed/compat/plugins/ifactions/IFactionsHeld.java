package host.plas.flyingallowed.compat.plugins.ifactions;

import host.plas.bou.compat.HeldHolder;

public class IFactionsHeld extends HeldHolder {
    public static String IDENTIFIER = "ImprovedFactions";

    public IFactionsHeld() {
        super(IDENTIFIER, new IFactionsHolderCreator());
    }
}
