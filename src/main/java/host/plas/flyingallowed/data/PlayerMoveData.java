package host.plas.flyingallowed.data;

import host.plas.bou.commands.Sender;
import host.plas.flyingallowed.FlyingAllowed;
import host.plas.flyingallowed.compat.*;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import tv.quaint.objects.SingleSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

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
            topLocation = topLocation.subtract(0, 1, 0);

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
            SingleSet<FlightAbility, FlightExtent> set = CompatManager.getFlyingAllowed(this);

            FlightAbility ability = set.getKey();
            FlightExtent extent = set.getValue();

            if (ability == FlightAbility.ABLE_TO_FLY || ability == FlightAbility.UNABLE_TO_FLY || ability == FlightAbility.NO_CLAIM) {
                if (player.hasPermission(FlyingAllowed.getMainConfig().getLandsToggleOnPerm())) {
                    if (checkFlyAndIsHandled(ability, extent, FlightFlag.TOGGLE_ALLOWED)) return;
                } else if (checkFlyAndIsHandled(ability, extent)) return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (! player.getAllowFlight() && hasPermission() && checkBypassPermissionOn() && checkSoftBypassPermissionOn()) {
            player.setAllowFlight(true);

            Sender sender = new Sender(player);
            sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOnWorldMessage());
        } else if (player.getAllowFlight() && ! hasPermission() && checkBypassPermissionOff() && checkSoftBypassPermissionOff()) {
            player.setFlying(false);
            player.setAllowFlight(false);
            teleportTopLocation();

            Sender sender = new Sender(player);
            sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOffWorldMessage());
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
                if (! player.getAllowFlight() && checkBypassPermissionOn() && checkSoftBypassPermissionOn()) {
                    FlyingAllowed.getInstance().logDebug("Toggling flight on for " + player.getName() + " by extent: " + extent.name());

                    player.setAllowFlight(true);

                    Sender sender = new Sender(player);
                    if (extent == FlightExtent.WORLDGUARD) {
                        sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOnRegionMessage());
                    } else {
                        sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOnClaimMessage());
                    }
                }
            }
            return true;
        } else if (ability == FlightAbility.UNABLE_TO_FLY || ability == FlightAbility.NO_CLAIM) {
            if (player.getAllowFlight() && checkBypassPermissionOff() && checkSoftBypassPermissionOff()) {
                FlyingAllowed.getInstance().logDebug("Toggling flight off for " + player.getName() + " by extent: " + extent.name());

                player.setFlying(false);
                player.setAllowFlight(false);
                teleportTopLocation();

                Sender sender = new Sender(player);
                if (extent == FlightExtent.WORLDGUARD) {
                    sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOffRegionMessage());
                } else {
                    sender.sendMessage(FlyingAllowed.getMessageConfig().getToggleOffClaimMessage());
                }
            }
            return true;
        }

        return false;
    }

    public void teleportTopLocation() {
        try {
            player.teleport(getTopLocation());
        } catch (Exception e) {
            try {
                player.teleportAsync(getTopLocation());
            } catch (Exception e2) {
                FlyingAllowed.getInstance().logWarningWithInfo("Unable to teleport player due to exception: " + e2.getMessage(), e2);
            }
        }
    }

    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    public boolean hasFlyInClaimPermission() {
        return hasPermission(FlyingAllowed.getMainConfig().getFlyInClaimsPermission()) ||
                hasPermission("flyingallowed.in.lands"); // For old versions of FlyingAllowed
    }

    public boolean hasFlyInRegionPermission() {
        return hasPermission(FlyingAllowed.getMainConfig().getFlyInRegionsPermission());
    }
}
