package host.plas.flyingallowed.compat;

import gg.drak.thebase.objects.SingleSet;
import host.plas.bou.compat.EmptyHolder;
import host.plas.bou.compat.HeldHolder;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.plugins.FlyingHolder;
import host.plas.flyingallowed.data.FlightAbility;
import host.plas.flyingallowed.data.FlightExtent;
import host.plas.flyingallowed.data.PlayerMoveData;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Getter @Setter
public class CompatManager {
    public static void init() {
        HookedPlugins.init();
    }

    public static void forHolder(String identifier, Supplier<HeldHolder> supplier) {
        HeldHolder holder = new EmptyHolder(identifier);
        try {
            holder = supplier.get();
            FlyingAllowed.getInstance().logInfo(identifier + " found, enabling...");
        } catch (Throwable e) {
            FlyingAllowed.getInstance().logInfo(identifier + " not found, skipping...");
        }
        putHolder(identifier, holder);
    }

    public static void onDisable() {
        for (HeldHolder holder : host.plas.bou.compat.CompatManager.getHolders().values()) {
            if (holder.getHolder() instanceof FlyingHolder) {
                FlyingHolder<?> flyingHolder = (FlyingHolder<?>) holder.getHolder();
                host.plas.bou.compat.CompatManager.unregisterHolder(flyingHolder.getIdentifier());
            }
        }
    }

    public static void putHolder(String identifier, HeldHolder holder) {
        host.plas.bou.compat.CompatManager.getHolders().put(identifier, holder);
    }

    public static HeldHolder getHolder(String identifier) {
        return host.plas.bou.compat.CompatManager.getHolders().get(identifier);
    }

    public static boolean isEnabled(String identifier) {
        return getHolder(identifier) != null && getHolder(identifier).isEnabled();
    }

    public static SingleSet<FlightAbility, FlightExtent> getFlyingAllowed(PlayerMoveData data) {
        List<SingleSet<FlightAbility, FlightExtent>> list = new ArrayList<>();

        host.plas.bou.compat.CompatManager.getHolders().values().stream()
                .filter(h -> h.getHolder() instanceof FlyingHolder)
                .forEach(holder -> {
                    if (! (holder.getHolder() instanceof FlyingHolder)) return;
                    FlyingHolder<?> flyingHolder = (FlyingHolder<?>) holder.getHolder();

                    if (holder.isEnabled()) {
                        SingleSet<FlightAbility, FlightExtent> set = flyingHolder.wrapFlyable(data);
                        if (set != null) {
                            list.add(set);
                        }
                    }
                });

        SingleSet<FlightAbility, FlightExtent> result = new SingleSet<>(FlightAbility.NONE, FlightExtent.NONE);
        for (SingleSet<FlightAbility, FlightExtent> set : list) {
            result = checkAndSet(set, result);
        }

        return result;
    }

    public static SingleSet<FlightAbility, FlightExtent> checkAndSet
            (SingleSet<FlightAbility, FlightExtent> set, SingleSet<FlightAbility, FlightExtent> result) {
        FlightAbility ability = set.getKey();
        FlightExtent extent = set.getValue();

        if (result.getKey() == FlightAbility.ABLE_TO_FLY) return result;
        if (ability == FlightAbility.ABLE_TO_FLY) {
            result = set;
        }

        if (result.getKey() == FlightAbility.UNABLE_TO_FLY) return result;
        if (ability == FlightAbility.UNABLE_TO_FLY) {
            result = set;
        }

        if (result.getKey() == FlightAbility.NO_CLAIM) return result;
        if (ability == FlightAbility.NO_CLAIM) {
            result = set;
        }

        return result;
    }
}