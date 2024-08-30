package host.plas.flyingallowed.compat.plugins.sskyblock;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class SSkyblockHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public SSkyblockHolder get() {
        return new SSkyblockHolder();
    }
}
