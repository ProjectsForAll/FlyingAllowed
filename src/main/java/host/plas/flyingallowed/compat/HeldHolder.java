package host.plas.flyingallowed.compat;

import lombok.Getter;
import lombok.Setter;
import tv.quaint.objects.Identifiable;

import java.util.function.Supplier;

@Getter @Setter
public class HeldHolder implements Identifiable {
    private String identifier;
    private Supplier<ApiHolder<?>> supplier;
    private ApiHolder<?> holder;
    private boolean enabled;

    public HeldHolder(String identifier, Supplier<ApiHolder<?>> supplier) {
        this.identifier = identifier;
        this.supplier = supplier;

        try {
            this.holder = supplier.get();
            this.enabled = true;
        } catch (Exception e) {
//            e.printStackTrace();
            this.holder = null;
            this.enabled = false;
        }
    }
}
