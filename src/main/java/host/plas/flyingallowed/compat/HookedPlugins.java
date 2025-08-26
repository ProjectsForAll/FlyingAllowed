package host.plas.flyingallowed.compat;

import host.plas.bou.compat.ApiHolder;
import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.compat.plugins.gdefender.GDefenderHeld;
import host.plas.flyingallowed.compat.plugins.gp.GPHeld;
import host.plas.flyingallowed.compat.plugins.hclaims.HClaimsHeld;
import host.plas.flyingallowed.compat.plugins.kingdoms.KingdomsHeld;
import host.plas.flyingallowed.compat.plugins.lands.LandsHeld;
import host.plas.flyingallowed.compat.plugins.lands.LandsHolder;
import host.plas.flyingallowed.compat.plugins.pstones.PStonesHeld;
import host.plas.flyingallowed.compat.plugins.sskyblock.SSkyblockHeld;
import host.plas.flyingallowed.compat.plugins.wg.WGHeld;
import lombok.Getter;

import java.util.Arrays;
import java.util.function.Supplier;

@Getter
public enum HookedPlugins {
    LANDS_IDENTIFIER(LandsHeld.IDENTIFIER, LandsHeld::new),
    GRIEF_PREVENTION_IDENTIFIER(GPHeld.IDENTIFIER, GPHeld::new),
    KINGDOMS_IDENTIFIER(KingdomsHeld.IDENTIFIER, KingdomsHeld::new),
    SS_IDENTIFIER(SSkyblockHeld.IDENTIFIER, SSkyblockHeld::new),
    PS_IDENTIFIER(PStonesHeld.IDENTIFIER, PStonesHeld::new),
    HCLAIMS_IDENTIFIER(HClaimsHeld.IDENTIFIER, HClaimsHeld::new),
    WG_IDENTIFIER(WGHeld.IDENTIFIER, WGHeld::new),
    GRIEF_DEFENDER_IDENTIFIER(GDefenderHeld.IDENTIFIER, GDefenderHeld::new),
    ;

    private final String identifier;
    private final Supplier<HeldHolder> supplier;

    HookedPlugins(String identifier, Supplier<HeldHolder> supplier) {
        this.identifier = identifier;
        this.supplier = supplier;
    }

    public void instantiate() {
        CompatManager.forHolder(getIdentifier(), getSupplier());
    }

    public ApiHolder<?> getHolder() {
        return CompatManager.getHolder(getIdentifier()).getHolder();
    }

    public boolean isEnabled() {
        return CompatManager.isEnabled(getIdentifier());
    }

    public static void init() {
        Arrays.stream(values()).forEach(HookedPlugins::instantiate);
    }
}
