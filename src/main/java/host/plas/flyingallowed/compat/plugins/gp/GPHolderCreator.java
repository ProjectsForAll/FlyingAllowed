package host.plas.flyingallowed.compat.plugins.gp;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class GPHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public GPHolder get() {
        return new GPHolder();
    }
}
