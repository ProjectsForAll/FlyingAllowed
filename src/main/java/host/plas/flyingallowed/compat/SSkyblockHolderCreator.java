package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class SSkyblockHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public SSkyblockHolder get() {
        return new SSkyblockHolder();
    }
}
