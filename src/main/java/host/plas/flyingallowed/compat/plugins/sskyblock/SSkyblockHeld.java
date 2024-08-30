package host.plas.flyingallowed.compat.plugins.sskyblock;

import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.CompatManager;

public class SSkyblockHeld extends HeldHolder {
    public SSkyblockHeld() {
        super(CompatManager.SS_IDENTIFIER, new SSkyblockHolderCreator());
    }
}
