package host.plas.flyingallowed.compat.integrated;

import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.ApiHolder;
import host.plas.flyingallowed.data.PlayerMoveData;
import me.angeschossen.lands.api.LandsIntegration;
import me.angeschossen.lands.api.flags.type.Flags;
import me.angeschossen.lands.api.flags.type.RoleFlag;
import me.angeschossen.lands.api.land.LandWorld;
import me.angeschossen.lands.api.player.LandPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Function;

public class LandsHolder extends ApiHolder<LandsIntegration> {
    public enum LandsAbility {
        NO_API,
        ABLE_TO_FLY,
        UNABLE_TO_FLY,
        ISSUE,
        ;
    }

    public LandsHolder() {
        super("lands", (v) -> LandsIntegration.of(FlyingAllowed.getInstance()));
    }

    public LandsAbility isFlyableAtLocation(PlayerMoveData moveData) {
        if (! isEnabled()) return LandsAbility.NO_API;

        LandWorld toLandWorld = api().getWorld(moveData.getToWorld());
        if (toLandWorld == null) return LandsAbility.ISSUE;

        if (toLandWorld.hasRoleFlag(moveData.getPlayer().getUniqueId(), moveData.getPlayer().getLocation(), Flags.FLY)) {
            return LandsAbility.ABLE_TO_FLY;
        } else {
            return LandsAbility.UNABLE_TO_FLY;
        }
    }
}
