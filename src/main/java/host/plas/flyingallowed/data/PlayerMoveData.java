package host.plas.flyingallowed.data;

import io.streamlined.bukkit.commands.Sender;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

@Getter @Setter
public class PlayerMoveData {
    @NonNull
    private PlayerMoveEvent event;

    @NonNull
    private Location from;
    @NonNull
    private Location to;
    @NonNull
    private World fromWorld;
    @NonNull
    private World toWorld;

    @NonNull
    private Player player;

    public PlayerMoveData(@NonNull PlayerMoveEvent event, @NonNull Location from, @NonNull Location to,
                          @NonNull World fromWorld, @NonNull World toWorld, @NonNull Player player) {
        this.event = event;
        this.from = from;
        this.to = to;
        this.fromWorld = fromWorld;
        this.toWorld = toWorld;
        this.player = player;
    }

    public boolean isSameWorld() {
        return fromWorld.equals(toWorld);
    }

    public boolean isDifferentWorld() {
        return ! isSameWorld();
    }

    public Location getTopLocation() {
        Location topLocation = to.clone();

        int iterations = 0;
        while (isTopable(topLocation)) {
            topLocation = topLocation.add(0, -1, 0);

            iterations ++;
        }

        if (iterations > 0) {
            while (! isTopable(topLocation)) {
                topLocation = topLocation.add(0, 1, 0);
            }
        } else {
            topLocation = topLocation.add(0, 1, 0);
        }

        return topLocation;
    }

    public static boolean isTopable(Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        return location.getBlock().isEmpty() & location.getBlockY() < world.getMaxHeight() && location.getBlockY() > world.getMinHeight();
    }

    public void checkPermission() {
        if (! hasPermission()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.teleport(getTopLocation());

            Sender sender = new Sender(player);
            sender.sendMessage("&eToggling &bflight &coff &eas you &cdo not have &epermission to &dfly &ein this &bworld&8!");
        } else {
            player.setAllowFlight(true);

            Sender sender = new Sender(player);
            sender.sendMessage("&eToggling &bflight &aon &eas you &ahave &epermission to &dfly &ein this &bworld&8!");
        }
    }

    public String getWorldPermission() {
        return "flyingallowed.allow." + toWorld.getName();
    }

    public boolean hasPermission() {
        return player.hasPermission(getWorldPermission());
    }
}
