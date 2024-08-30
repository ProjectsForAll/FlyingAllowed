package host.plas.flyingallowed.compat.plugins.pstones;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class PStonesHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public PStonesHolder get() {
        return new PStonesHolder();
    }
}
