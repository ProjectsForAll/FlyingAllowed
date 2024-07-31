package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.compat.HolderCreator;

public class LandsHolderCreator implements HolderCreator {
    @Override
    public LandsHolder get() {
        return new LandsHolder();
    }
}
