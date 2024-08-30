package host.plas.flyingallowed.compat.plugins.lands;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class LandsHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public LandsHolder get() {
        return new LandsHolder();
    }
}
