package host.plas.flyingallowed.compat.plugins.gdefender;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class GDefenderHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public GDefenderHolder get() {
        return new GDefenderHolder();
    }
}
