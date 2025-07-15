package host.plas.flyingallowed.compat.plugins;

import gg.drak.thebase.objects.SingleSet;
import host.plas.bou.compat.ApiHolder;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Function;

@Getter @Setter
public abstract class FlyingHolder<P> extends ApiHolder<P> {
    private FlightExtent extent;

    public FlyingHolder(String identifier, Function<Void, P> getter, FlightExtent extent) {
        super(identifier, getter);

        this.extent = extent;
    }

    public SingleSet<FlightAbility, FlightExtent> wrapFlyable(PlayerMoveData moveData) {
        FlightAbility ability = isFlyableAtLocation(moveData);

        return new SingleSet<>(ability, extent);
    }

    abstract public FlightAbility isFlyableAtLocation(PlayerMoveData moveData);

    public static String getAllClaimsPermission() {
        return FlyingAllowed.getMainConfig().getFlyInClaimsAllPermission();
    }

    public static boolean hasAllClaimsPermission(PlayerMoveData moveData) {
        return moveData.hasPermission(getAllClaimsPermission());
    }
}
