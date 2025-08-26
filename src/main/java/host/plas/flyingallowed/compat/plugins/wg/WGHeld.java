package host.plas.flyingallowed.compat.plugins.wg;

import host.plas.bou.compat.HeldHolder;

public class WGHeld extends HeldHolder {
    public static String IDENTIFIER = "WorldGuard";

    public WGHeld() {
        super(IDENTIFIER, new WGHolderCreator());
    }
}
