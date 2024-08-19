package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;

import java.util.function.Supplier;

public class KingdomsHolderCreator implements Supplier<ApiHolder<?>> {
    @Override
    public KingdomsHolder get() {
        return new KingdomsHolder();
    }
}
