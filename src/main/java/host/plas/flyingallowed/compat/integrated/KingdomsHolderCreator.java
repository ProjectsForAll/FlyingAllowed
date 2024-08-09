package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.compat.HolderCreator;

public class KingdomsHolderCreator implements HolderCreator {
    @Override
    public KingdomsHolder get() {
        return new KingdomsHolder();
    }
}
