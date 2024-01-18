package host.plas.flyingallowed.data;

import host.plas.flyingallowed.FlyingAllowed;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@Getter @Setter
public class FlightWorlds implements Listener {
    public FlightWorlds() {
        register();
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, FlyingAllowed.getInstance());
    }

    public void unregister() {
        PlayerMoveEvent.getHandlerList().unregister(this);
        PlayerTeleportEvent.getHandlerList().unregister(this);
        PlayerPortalEvent.getHandlerList().unregister(this);
    }

    public void onSameWorld(PlayerMoveData data) {
        data.checkPermission();
    }

    public void onDifferentWorld(PlayerMoveData data) {
        data.checkPermission();
    }

    public void onMoveWorld(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from == null || to == null) return;

        World fromWorld = from.getWorld();
        World toWorld = to.getWorld();

        if (fromWorld == null || toWorld == null) return;

        PlayerMoveData data = new PlayerMoveData(event, from, to, fromWorld, toWorld, event.getPlayer());

        if (data.isSameWorld()) {
            // Player is moving within the same world.
            onSameWorld(data);
        }

        if (data.isDifferentWorld()) {
            // Player is leaving the flight world.
            onDifferentWorld(data);
        }
    }

    @EventHandler
    public void onMoveToWorld(PlayerMoveEvent event) {
        onMoveWorld(event);
    }

    @EventHandler
    public void onTeleportToWorld(PlayerTeleportEvent event) {
        onMoveWorld(event);
    }

    @EventHandler
    public void onPortalToWorld(PlayerPortalEvent event) {
        onMoveWorld(event);
    }
}
