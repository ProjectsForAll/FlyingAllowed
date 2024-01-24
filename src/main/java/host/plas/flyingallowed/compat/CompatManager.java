package host.plas.flyingallowed.compat;

import host.plas.flyingallowed.compat.integrated.LandsHolder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompatManager {
    @Getter @Setter
    private static LandsHolder landsHolder;

    public static void init() {
        setLandsHolder(new LandsHolder());
    }
}
