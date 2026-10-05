package host.plas.flyingallowed.compat.plugins.fskyblock;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class FSkyblockHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public FSkyblockHolder get() {
        return new FSkyblockHolder();
    }
}
