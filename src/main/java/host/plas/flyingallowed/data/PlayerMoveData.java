package host.plas.flyingallowed.data;

import host.plas.bou.commands.Sender;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.CompatManager;
import host.plas.flyingallowed.compat.integrated.GriefPreventionHolder;
import host.plas.flyingallowed.compat.integrated.LandsHolder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;

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

    public boolean checkBypassPermissionOn() {
        return FlyingAllowed.getMainConfig().getAutoToggleOnEnabled() && ! player.hasPermission(FlyingAllowed.getMainConfig().getAutoToggleOnBypassPerm());
    }

    public boolean checkBypassPermissionOff() {
        return FlyingAllowed.getMainConfig().getAutoToggleOffEnabled() && ! player.hasPermission(FlyingAllowed.getMainConfig().getAutoToggleOffBypassPerm());
    }

    public boolean isSoftBypassing() {
        return player.hasPermission(FlyingAllowed.getMainConfig().getSoftBypassingPerm()) && FlyingAllowed.getMainConfig().isBypassing(player);
    }

    public boolean checkSoftBypassPermissionOn() {
        return FlyingAllowed.getMainConfig().getAutoToggleOnEnabled() && ! isSoftBypassing();
    }

    public boolean checkSoftBypassPermissionOff() {
        return FlyingAllowed.getMainConfig().getAutoToggleOffEnabled() && ! isSoftBypassing();
    }

    public void checkPermission() {
        GameMode gameMode = player.getGameMode();
        if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) return;

        try {
            if (CompatManager.isLandsEnabled()) {
                FlightAbility ability = ((LandsHolder) CompatManager.getLandsHolder().getHolder()).isFlyableAtLocation(this);

                if (ability == FlightAbility.ABLE_TO_FLY || ability == FlightAbility.UNABLE_TO_FLY) {
                    if (player.hasPermission(FlyingAllowed.getMainConfig().getLandsToggleOnPerm())) {
                        if (checkFlyAndIsHandled(ability, FlightExtent.LANDS, FlightFlag.TOGGLE_ALLOWED)) return;
                    } else if (checkFlyAndIsHandled(ability, FlightExtent.LANDS)) return;
                }
            }
            if (CompatManager.isGriefPreventionEnabled()) {
                FlightAbility ability = ((GriefPreventionHolder) CompatManager.getGriefPreventionHolder().getHolder()).isFlyableAtLocation(this);

                if (ability == FlightAbility.ABLE_TO_FLY || ability == FlightAbility.UNABLE_TO_FLY) {
                    if (player.hasPermission(FlyingAllowed.getMainConfig().getLandsToggleOnPerm())) {
                        if (checkFlyAndIsHandled(ability, FlightExtent.GRIEF_PREVENTION, FlightFlag.TOGGLE_ALLOWED)) return;
                    } else if (checkFlyAndIsHandled(ability, FlightExtent.GRIEF_PREVENTION)) return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (! player.getAllowFlight() && hasPermission() && checkBypassPermissionOn() && checkSoftBypassPermissionOn()) {
            player.setAllowFlight(true);

            Sender sender = new Sender(player);
            sender.sendMessage("&eToggling &bflight &aon &eas you &ahave &epermission to &dfly &ein this &bworld&8!");
        } else if (player.getAllowFlight() && ! hasPermission() && checkBypassPermissionOff() && checkSoftBypassPermissionOff()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            player.teleport(getTopLocation());

            Sender sender = new Sender(player);
            sender.sendMessage("&eToggling &bflight &coff &eas you &cdo not have &epermission to &dfly &ein this &bworld&8!");
        }
    }

    public String getWorldPermission() {
        return "flyingallowed.allow." + toWorld.getName();
    }

    public boolean hasPermission() {
        return player.hasPermission(getWorldPermission());
    }

    public boolean checkFlyAndIsHandled(FlightAbility ability, FlightExtent extent, FlightFlag... flags) {
        return checkFlyAndIsHandled(ability, extent, new ArrayList<>(List.of(flags)));
    }

    public boolean checkFlyAndIsHandled(FlightAbility ability, FlightExtent extent, List<FlightFlag> flags) {
        if (ability == FlightAbility.ABLE_TO_FLY) {
            if (flags.contains(FlightFlag.TOGGLE_ALLOWED)) {
                if (!player.getAllowFlight() && checkBypassPermissionOn() && checkSoftBypassPermissionOn()) {
                    player.setAllowFlight(true);

                    Sender sender = new Sender(player);
                    sender.sendMessage("&eToggling &bflight &aon &eas you are &aable to &dfly &ein this &bclaim&8!");
                }
            }
            return true;
        } else if (ability == FlightAbility.UNABLE_TO_FLY) {
            if (player.getAllowFlight() && checkBypassPermissionOff() && checkSoftBypassPermissionOff()) {
                player.setFlying(false);
                player.setAllowFlight(false);
                player.teleport(getTopLocation());

                Sender sender = new Sender(player);
                sender.sendMessage("&eToggling &bflight &coff &eas you are &cunable to &dfly &ein this &bclaim&8!");
            }
            return true;
        }

        return false;
    }
}
