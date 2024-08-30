package host.plas.flyingallowed.compat.plugins.kingdoms;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class KingdomsHeld extends HeldHolder {
    public KingdomsHeld() {
        super(CompatManager.KINGDOMS_IDENTIFIER, new KingdomsHolderCreator());
    }
}
