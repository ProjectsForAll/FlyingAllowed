package host.plas.flyingallowed.data;

import org.bukkit.entity.Player;

public final class FlightState {
    private FlightState() {
    }

    public static boolean isEnabled(Player player) {
        return player.getAllowFlight() || player.isFlying();
    }

    public static boolean enable(Player player) {
        if (isEnabled(player)) return false;

        player.setAllowFlight(true);
        return true;
    }
}
