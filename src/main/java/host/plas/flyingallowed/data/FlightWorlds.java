package host.plas.flyingallowed.data;

import host.plas.flyingallowed.FlyingAllowed;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
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
        HandlerList.unregisterAll(this);
    }

    public void onSameWorld(PlayerMoveData data) {
        data.checkPermission();
    }

    public void onDifferentWorld(PlayerMoveData data) {
        MoveDataCache.remove(data.getPlayer());
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

    /**
     * Whether a move stays inside the same block. Claims and regions are resolved per block,
     * so such a move (including a pure head rotation) cannot change the verdict.
     */
    public static boolean isSameBlock(Location from, Location to) {
        if (to == null || from.getWorld() != to.getWorld()) return false;

        return from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        MoveDataCache.remove(event.getPlayer());
    }

    // Both reset allow-flight for survival players, so the last verdict no longer reflects the
    // player's state and the next move must be free to re-enable flight.
    @EventHandler(ignoreCancelled = true)
    public void onGameModeChange(PlayerGameModeChangeEvent event) {
        MoveDataCache.remove(event.getPlayer());
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        MoveDataCache.remove(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoveToWorld(PlayerMoveEvent event) {
        if (isSameBlock(event.getFrom(), event.getTo())) return;

        onMoveWorld(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onTeleportToWorld(PlayerTeleportEvent event) {
        onMoveWorld(event);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortalToWorld(PlayerPortalEvent event) {
        onMoveWorld(event);
    }
}
