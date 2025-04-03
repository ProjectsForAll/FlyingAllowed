package host.plas.flyingallowed.compat.plugins.wg;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class WGHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public WGHolder get() {
        return new WGHolder();
    }
}
