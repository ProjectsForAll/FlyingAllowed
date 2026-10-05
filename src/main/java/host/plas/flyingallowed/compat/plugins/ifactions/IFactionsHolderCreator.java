package host.plas.flyingallowed.compat.plugins.ifactions;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class IFactionsHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public IFactionsHolder get() {
        return new IFactionsHolder();
    }
}
