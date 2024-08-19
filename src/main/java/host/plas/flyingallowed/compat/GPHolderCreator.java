package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class GPHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public GriefPreventionHolder get() {
        return new GriefPreventionHolder();
    }
}
