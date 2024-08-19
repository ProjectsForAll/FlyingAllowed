package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class LandsHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public LandsHolder get() {
        return new LandsHolder();
    }
}
