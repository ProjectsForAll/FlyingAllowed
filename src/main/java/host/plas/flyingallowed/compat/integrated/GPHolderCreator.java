package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.compat.HolderCreator;

public class GPHolderCreator implements HolderCreator {
    @Override
    public GriefPreventionHolder get() {
        return new GriefPreventionHolder();
    }
}
