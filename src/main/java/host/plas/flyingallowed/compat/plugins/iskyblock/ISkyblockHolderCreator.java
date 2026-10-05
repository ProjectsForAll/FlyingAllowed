package host.plas.flyingallowed.compat.plugins.iskyblock;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class ISkyblockHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public ISkyblockHolder get() {
        return new ISkyblockHolder();
    }
}
