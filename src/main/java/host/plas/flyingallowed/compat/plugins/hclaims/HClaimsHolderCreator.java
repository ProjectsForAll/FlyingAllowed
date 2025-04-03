package host.plas.flyingallowed.compat.plugins.hclaims;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class HClaimsHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public HClaimsHolder get() {
        return new HClaimsHolder();
    }
}
